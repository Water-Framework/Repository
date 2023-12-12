
/*
 Copyright 2019-2023 ACSoftware

 Licensed under the Apache License, Version 2.0 (the "License")
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */

package it.water.repository.service;

import it.water.core.api.entity.events.*;
import it.water.core.api.model.BaseEntity;
import it.water.core.api.model.PaginableResult;
import it.water.core.api.model.events.ApplicationEventProducer;
import it.water.core.api.model.events.Event;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.repository.BaseRepository;
import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.QueryBuilder;
import it.water.core.api.repository.query.QueryOrder;
import it.water.core.api.service.BaseEntitySystemApi;
import it.water.repository.entity.model.exceptions.DuplicateEntityException;
import it.water.repository.entity.model.exceptions.EntityNotFound;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.model.exceptions.WaterRuntimeException;
import it.water.core.registry.model.exception.NoComponentRegistryFoundException;
import it.water.core.service.BaseAbstractSystemService;
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
        try {
            produceEvent(entity, PreSaveEvent.class);
            this.getRepository().persist(entity);
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
        try {
            T entityBeforeUpdate = find(entity.getId());
            produceEvent(entity, PreUpdateEvent.class);
            produceDetailedEvent(entityBeforeUpdate, entity, PreUpdateDetailedEvent.class);
            this.getRepository().update(entity);
            produceDetailedEvent(entityBeforeUpdate, entity, PostUpdateDetailedEvent.class);
            return entity;
        } catch (DuplicateEntityException e) {
            getLog().warn("Update failed: entity is duplicated!");
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
            this.getRepository().remove(id);
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
            log.warn("No Event Producer Found for this project, skipping producing event {}", eventClass);
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
            log.warn("No Event Producer Found for this project, skipping producing event {}", eventClass);
        }
    }

    /**
     *
     * @return query builder instance
     */
    @Override
    public QueryBuilder getQueryBuilderInstance() {
        return getRepository().getQueryBuilderInstance();
    }

    /**
     * Return the current repository
     */
    protected abstract BaseRepository<T> getRepository();

}
