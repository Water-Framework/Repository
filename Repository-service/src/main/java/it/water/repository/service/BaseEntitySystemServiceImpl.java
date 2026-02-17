
/*
 * Copyright 2024 Aristide Cittadino
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.water.repository.service;

import it.water.core.api.service.integration.AssetCategoryIntegrationClient;
import it.water.core.api.service.integration.AssetTagIntegrationClient;
import it.water.core.api.entity.events.*;
import it.water.core.api.model.*;
import it.water.core.api.model.events.ApplicationEventProducer;
import it.water.core.api.model.events.Event;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.repository.BaseRepository;
import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.QueryBuilder;
import it.water.core.api.repository.query.QueryOrder;
import it.water.core.api.service.BaseEntitySystemApi;
import it.water.core.api.validation.WaterValidator;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.model.exceptions.WaterRuntimeException;
import it.water.core.registry.model.exception.NoComponentRegistryFoundException;
import it.water.core.service.BaseAbstractSystemService;
import it.water.repository.entity.model.exceptions.DuplicateEntityException;
import it.water.repository.entity.model.exceptions.EntityNotFound;
import it.water.repository.entity.model.exceptions.NoResultException;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @param <T> parameter that indicates a generic class
 * @Author Aristide Cittadino.
 * Interface component for WaterBaseEntityApi.
 * This interface defines the methods for basic CRUD operations. This
 * methods are reusable by all entities in order to interact with the
 * persistence layer.
 */
public abstract class BaseEntitySystemServiceImpl<T extends BaseEntity>
        extends BaseAbstractSystemService implements BaseEntitySystemApi<T> {
    private static Logger log = LoggerFactory.getLogger(BaseEntitySystemServiceImpl.class);

    /**
     * Generic class for Water platform
     */
    private Class<T> type;

    @Inject
    @Setter
    protected ComponentRegistry componentRegistry;

    @Inject
    @Setter
    protected WaterValidator waterValidator;

    @Inject
    @Setter
    private AssetCategoryIntegrationClient assetCategoryIntegrationClient;

    @Inject
    @Setter
    private AssetTagIntegrationClient assetTagIntegrationClient;

    /**
     * Constructor for WaterBaseEntitySystemServiceImpl
     *
     * @param type parameter that indicates a generic entity
     */
    protected BaseEntitySystemServiceImpl(Class<T> type) {
        this.type = type;
    }

    /**
     * Save an entity in database
     *
     * @param entity parameter that indicates a generic entity
     * @return entity saved
     */
    @Override
    public T save(T entity) {
        getLog().debug(
                "System Service Saving entity {}: {}", this.type.getSimpleName(), entity);
        //throws runtime exception if validation is not met
        this.validate(entity);
        this.validateEntityExtension(entity);
        try {
            produceEvent(entity, PreSaveEvent.class);
            //Save the entity and if it has expansion proceed to invoke the save of the extension in the same transaction
            this.getRepository().persist(entity);
            manageAssets(entity, AssetOperation.ADD);
            produceEvent(entity, PostSaveEvent.class);
            return entity;
        } catch (DuplicateEntityException e) {
            getLog().warn("Save failed: entity is duplicated!");
            throw e;
        } catch (Exception e1) {
            throw new WaterRuntimeException(e1.getMessage());
        }
    }

    /**
     * Update an existing entity in database
     *
     * @param entity parameter that indicates a generic entity
     */
    @Override
    public T update(T entity) {
        getLog().debug(
                "System Service Updating entity {}: {}", this.type.getSimpleName(), entity);
        //throws runtime exception if validation is not met
        this.validate(entity);
        this.validateEntityExtension(entity);
        try {
            T entityBeforeUpdate = find(entity.getId());
            produceEvent(entity, PreUpdateEvent.class);
            produceDetailedEvent(entityBeforeUpdate, entity, PreUpdateDetailedEvent.class);
            //updates the entity and process, eventually the expandable entity
            T updatedEntity = this.getRepository().update(entity);
            manageAssets(entity, AssetOperation.UPDATE);
            produceDetailedEvent(entityBeforeUpdate, updatedEntity, PostUpdateDetailedEvent.class);
            return updatedEntity;
        } catch (DuplicateEntityException e) {
            getLog().warn("Update failed: entity is duplicated!");
            throw e;
        } catch (NoResultException e) {
            getLog().warn("Update failed: entity to update not found!");
            throw e;
        } catch (Exception e1) {
            throw new WaterRuntimeException(e1.getMessage());
        }
    }

    /**
     * Remove an entity in database
     *
     * @param id parameter that indicates a entity id
     */
    @Override
    public void remove(long id) {
        getLog().debug(
                "System Service Removing entity {} with id {}", this.type.getSimpleName(), id);
        T entity = find(id);
        if (entity != null) {
            produceEvent(entity, PreRemoveEvent.class);
            //removes the main entity and eventually the expansion entity
            this.getRepository().remove(id);
            manageAssets(entity, AssetOperation.DELETE);
            produceEvent(entity, PostRemoveEvent.class);
            return;
        }
        throw new EntityNotFound();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public T find(long id) {
        return this.getRepository().find(id);
    }

    /**
     * @param filter filter
     * @return
     */
    @Override
    public T find(Query filter) {
        return this.getRepository().find(filter);
    }

    /**
     * @param filter
     * @param delta
     * @param page
     * @param queryOrder
     * @return
     */
    @Override
    public PaginableResult<T> findAll(Query filter, int delta,
                                      int page, QueryOrder queryOrder) {
        getLog().debug("System Service Finding All entities of {} with delta: {} num page:{} and with orderParameters", this.type.getSimpleName(), delta, page);
        return this.getRepository().findAll(delta, page, filter, queryOrder);
    }

    /**
     *
     * @param entity
     * @param operation
     */
    private void manageAssets(T entity, AssetOperation operation) {
        manageAssetCategories(entity, operation);
        manageAssetTags(entity, operation);
    }

    /**
     *
     * @param entity
     * @param op
     */
    private void manageAssetCategories(T entity, AssetOperation op) {
        if (assetCategoryIntegrationClient == null)
            return;

        String resourceName = entity.getClass().getName();
        long resourceId = entity.getId();
        long[] categoryIds = entity.getCategoryIds();
        switch (op) {
            case ADD:
                if (categoryIds != null && categoryIds.length > 0)
                    assetCategoryIntegrationClient.addAssetCategories(resourceName, resourceId,
                            categoryIds);
                break;
            case UPDATE:
                long[] oldIds = assetCategoryIntegrationClient.findAssetCategories(resourceName, resourceId);
                assetCategoryIntegrationClient.removeAssetCategories(resourceName, resourceId, oldIds);
                if (categoryIds != null && categoryIds.length > 0)
                    assetCategoryIntegrationClient.addAssetCategories(resourceName, resourceId,
                            categoryIds);
                break;
            case DELETE:
                long[] ids = assetCategoryIntegrationClient.findAssetCategories(resourceName, resourceId);
                assetCategoryIntegrationClient.removeAssetCategories(resourceName, resourceId, ids);
        }
    }

    /**
     * Manage asset tags for an entity
     * 
     * @param entity the entity
     * @param op     the operation (ADD, UPDATE, DELETE)
     */
    private void manageAssetTags(T entity, AssetOperation op) {
        if (assetTagIntegrationClient == null)
            return;

        String resourceName = entity.getClass().getName();
        long resourceId = entity.getId();
        long[] tagIds = entity.getTagIds();
        switch (op) {
            case ADD:
                if (tagIds != null && tagIds.length > 0)
                    assetTagIntegrationClient.addAssetTags(resourceName, resourceId,
                            tagIds);
                break;
            case UPDATE:
                long[] oldIds = assetTagIntegrationClient.findAssetTags(resourceName, resourceId);
                assetTagIntegrationClient.removeAssetTags(resourceName, resourceId, oldIds);
                if (tagIds != null && tagIds.length > 0)
                    assetTagIntegrationClient.addAssetTags(resourceName, resourceId,
                            tagIds);
                break;
            case DELETE:
                long[] ids = assetTagIntegrationClient.findAssetTags(resourceName, resourceId);
                assetTagIntegrationClient.removeAssetTags(resourceName, resourceId, ids);
        }
    }

    /**
     * Return current entity type
     */
    @Override
    public Class<T> getEntityType() {
        return this.type;
    }

    /**
     * @param filter filter
     * @return
     */
    @Override
    public long countAll(Query filter) {
        return this.getRepository().countAll(filter);
    }

    /**
     * @param entity
     * @param eventClass
     * @param <K>
     */
    private <K extends Event> void produceEvent(T entity, Class<K> eventClass) {
        try {
            ApplicationEventProducer eventProducer = this.componentRegistry.findComponent(ApplicationEventProducer.class, null);
            if (eventProducer != null)
                eventProducer.produceEvent(entity, eventClass); // execute pre actions after removing
        } catch (NoComponentRegistryFoundException e) {
            log.debug("No Event Producer Found for this project, skipping producing event {}", eventClass);
        }
    }

    /**
     * @param beforeUpdateEntity
     * @param entity
     * @param eventClass
     * @param <K>
     */
    private <K extends Event> void produceDetailedEvent(T beforeUpdateEntity, T entity, Class<K> eventClass) {
        try {
            ApplicationEventProducer eventProducer = this.componentRegistry.findComponent(ApplicationEventProducer.class, null);
            if (eventProducer != null)
                eventProducer.produceDetailedEvent(beforeUpdateEntity, entity, eventClass); // execute pre actions after removing
        } catch (NoComponentRegistryFoundException e) {
            log.debug("No Event Producer Found for this project, skipping producing event {}", eventClass);
        }
    }

    /**
     * @return query builder instance
     */
    @Override
    public QueryBuilder getQueryBuilderInstance() {
        return getRepository().getQueryBuilderInstance();
    }

    /**
     * Validation managed by registered WaterValidator Bean
     *
     * @param resource
     */
    @Override
    protected void validate(Resource resource) {
        if (this.waterValidator != null) {
            this.waterValidator.validate(resource);
        }
    }

    /**
     * Validates the expansion
     *
     * @param entity
     */
    private void validateEntityExtension(T entity) {
        //validating eventually the entity expansion
        EntityExtension extension = entity.isExpandableEntity()?((ExpandableEntity)entity).getExtension():null;
        if (extension != null)
            this.validate(extension);
    }

    /**
     * Return the current repository
     */
    protected abstract BaseRepository<T> getRepository();

    protected enum AssetOperation {
        ADD, DELETE, UPDATE;
    }
}
