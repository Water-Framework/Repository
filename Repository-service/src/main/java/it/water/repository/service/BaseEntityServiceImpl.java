
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


import it.water.core.api.bundle.Runtime;
import it.water.core.api.entity.owned.OwnedResource;
import it.water.core.api.entity.shared.SharedEntity;
import it.water.core.api.model.BaseEntity;
import it.water.core.api.model.PaginableResult;
import it.water.core.api.permission.SecurityContext;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.QueryOrder;
import it.water.core.api.service.BaseEntityApi;
import it.water.core.api.service.BaseEntitySystemApi;
import it.water.core.api.service.integration.SharedEntityIntegrationClient;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.permission.action.CrudActions;
import it.water.core.permission.annotations.AllowGenericPermissions;
import it.water.core.permission.annotations.AllowPermissions;
import it.water.core.permission.annotations.AllowPermissionsOnReturn;
import it.water.core.permission.exceptions.UnauthorizedException;
import it.water.core.registry.model.exception.NoComponentRegistryFoundException;
import it.water.core.service.BaseAbstractService;
import it.water.repository.entity.model.exceptions.EntityNotFound;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;


/**
 * @param <T> parameter that indicates a generic class.
 * @Author Aristide Cittadino.
 * Abstract Service class for Entity.
 * This class implements the methods for basic CRUD operations.
 * This methods are reusable by all entities in order to interact with the
 * system layer.
 */
public abstract class BaseEntityServiceImpl<T extends BaseEntity> extends BaseAbstractService implements BaseEntityApi<T> {
    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Inject
    @Setter
    @Getter(AccessLevel.PROTECTED)
    private Runtime runtime;

    /**
     * Generic class for  platform
     */
    private final Class<T> type;

    /**
     * Constructor for BaseEntityServiceImpl
     *
     * @param type parameter that indicates a generic entity
     */
    protected BaseEntityServiceImpl(Class<T> type) {
        this.type = type;
    }

    /**
     * Save an entity in datacore
     *
     * @param entity parameter that indicates a generic entity
     * @return entity saved
     */
    @AllowPermissions(actions = {CrudActions.SAVE})
    public T save(T entity) {
        this.log.debug("Service Saving entity {}: {}", this.type.getSimpleName(), entity);
        //automatic setting ownership on entity
        if (entity instanceof OwnedResource ownedResource) {
            Long ownerUserId = (runtime != null) ? runtime.getSecurityContext().getLoggedEntityId() : 0;
            ownedResource.setOwnerUserId(ownerUserId);
        }
        return this.getSystemService().save(entity);
    }

    /**
     * Update an existing entity in datacore
     *
     * @param entity parameter that indicates a generic entity
     */

    @AllowPermissions(actions = {CrudActions.UPDATE})
    public T update(T entity) {
        this.log.debug("Service Updating entity entity {}: {} ", this.type.getSimpleName(), entity);
        if (entity.getId() > 0) {
            // #38: on the generic update path the client must never be able to change the owner of an
            // OwnedResource (give it away or null it). Always restore ownerUserId from the currently
            // persisted entity before delegating to the system service. This is intentionally applied
            // to admins too: the generic update is not the place to transfer ownership (a dedicated
            // operation should be used for that). Note this only fixes the persisted field value; the
            // H5 ownership/permission interceptor independently reloads the entity for authorization.
            if (entity instanceof OwnedResource ownedResource) {
                // Restore the owner from the persisted entity when it exists; if it does not exist we
                // leave the entity unchanged and let the system-service update surface the error,
                // preserving the prior not-found behaviour of update().
                BaseEntity persisted = this.getSystemService().find(entity.getId());
                if (persisted instanceof OwnedResource persistedOwned) {
                    ownedResource.setOwnerUserId(persistedOwned.getOwnerUserId());
                }
            }
            return this.getSystemService().update(entity);
        }
        throw new EntityNotFound();
    }

    /**
     * Remove an entity in datacore
     *
     * @param id parameter that indicates a entity id
     */
    @AllowPermissions(actions = CrudActions.REMOVE, checkById = true)
    public void remove(long id) {
        this.log.debug("Service Removing entity {} with id {}", this.type.getSimpleName(), id);
        BaseEntity entity = this.getSystemService().find(id);
        if (entity != null) {
            this.getSystemService().remove(entity.getId());
            return;
        }
        throw new EntityNotFound();
    }

    /**
     * Find an existing entity in datacore
     *
     * @param id parameter that indicates a entity id
     * @return Entity if found
     */
    @AllowPermissions(actions = CrudActions.FIND, checkById = true)
    public T find(long id) {
        Query queryFilter = getSystemService().getQueryBuilderInstance().createQueryFilter("id=" + id);
        return this.find(queryFilter);
    }

    /**
     * @param filter filter
     * @return
     */
    @Override
    @AllowGenericPermissions(actions = {CrudActions.FIND})
    @AllowPermissionsOnReturn(actions = {CrudActions.FIND})
    public T find(Query filter) {
        this.log.debug("Service Find entity {} with id {}", this.type.getSimpleName(), filter);
        SecurityContext securityContext = runtime.getSecurityContext();
        filter = this.createConditionForOwnedOrSharedResource(filter, securityContext);
        return this.getSystemService().find(filter);
    }

    /**
     * @param queryOrder parameters that define order's criteria
     * @param filter     filter
     * @param delta
     * @param page
     * @return
     */
    @Override
    @AllowGenericPermissions(actions = CrudActions.FIND_ALL)
    public PaginableResult<T> findAll(Query filter, int delta, int page, QueryOrder queryOrder) {
        this.log.debug("Service Find all entities {} ", this.type.getSimpleName());
        SecurityContext securityContext = runtime.getSecurityContext();
        filter = this.createConditionForOwnedOrSharedResource(filter, securityContext);
        return this.getSystemService().findAll(filter, delta, page, queryOrder);
    }

    /**
     * @param initialFilter
     * @return
     */
    private Query createConditionForOwnedOrSharedResource(Query initialFilter, SecurityContext securityContext) {
        if (OwnedResource.class.isAssignableFrom(this.getEntityType())) {
            if (securityContext == null)
                throw new UnauthorizedException();
            //admins can see everything
            if (!securityContext.isAdmin()) {
                Query ownedResourceFilter = null;

                if (securityContext.getLoggedEntityId() != 0) {
                    ownedResourceFilter = getSystemService().getQueryBuilderInstance().field(OwnedResource.getOwnerUserIdFieldName()).equalTo(securityContext.getLoggedEntityId());
                } else {
                    throw new UnauthorizedException();
                }

                ownedResourceFilter = this.createFilterForOwnedOrSharedResource(ownedResourceFilter, securityContext.getLoggedEntityId());

                if (initialFilter == null) initialFilter = ownedResourceFilter;
                else if (ownedResourceFilter != null) {
                    initialFilter = initialFilter.and(ownedResourceFilter);
                }
            }
        }
        return initialFilter;
    }

    protected abstract BaseEntitySystemApi<T> getSystemService();

    /**
     * @return Component registry cored on the current technology or framework
     */
    protected abstract ComponentRegistry getComponentRegistry();

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
    @AllowGenericPermissions(actions = CrudActions.FIND)
    public long countAll(Query filter) {
        this.log.debug("Service countAll entities {}", this.type.getSimpleName());
        SecurityContext securityContext = runtime.getSecurityContext();
        filter = this.createConditionForOwnedOrSharedResource(filter, securityContext);
        return this.getSystemService().countAll(filter);
    }

    /**
     * Retrieve the SharedEntityIntegrationClient
     *
     * @return the SharedEntitySystemApi
     */
    protected SharedEntityIntegrationClient getSharedEntityIntegrationClient() {
        try {
            return getComponentRegistry().findComponent(SharedEntityIntegrationClient.class, null);
        } catch (NoComponentRegistryFoundException e) {
            getLog().warn("No shared entity integration client found!");
            return null;
        }
    }

    protected Query createFilterForOwnedOrSharedResource(Query ownedResourceFilter, long loggedEntityId) {
        if (SharedEntity.class.isAssignableFrom(this.getEntityType())) {
            SharedEntityIntegrationClient sharedEntityIntegrationClient = getSharedEntityIntegrationClient();
            if (sharedEntityIntegrationClient != null) {
                //forcing the condition that user must own the entity or is shared with him
                Collection<Long> entityIds = sharedEntityIntegrationClient.fetchSharingUsersIds(type.getName(), loggedEntityId);
                if (!entityIds.isEmpty()) {
                    // H7: parse only server-controlled numeric ids and combine via or(),
                    // never re-serialize the existing filter (injection risk).
                    // NOTE: field("id").in(list) is unusable here — In is capped at 2 operands
                    // and the JPA PredicateBuilder expects a FieldValueListOperand.
                    StringBuilder ids = new StringBuilder();
                    for (Long sharedId : entityIds) {
                        if (ids.length() > 0) {
                            ids.append(",");
                        }
                        ids.append(sharedId.longValue());
                    }
                    Query sharedByIdsFilter = getSystemService().getQueryBuilderInstance().createQueryFilter("id IN (" + ids + ")");
                    ownedResourceFilter = ownedResourceFilter.or(sharedByIdsFilter);
                }
            }
        }
        return ownedResourceFilter;
    }
}
