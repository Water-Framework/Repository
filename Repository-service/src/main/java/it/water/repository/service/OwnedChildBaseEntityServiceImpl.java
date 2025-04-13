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

import it.water.core.api.entity.owned.OwnedChildResource;
import it.water.core.api.entity.owned.OwnedResource;
import it.water.core.api.model.BaseEntity;
import it.water.core.api.repository.query.Query;
import it.water.core.api.service.BaseEntityApi;
import it.water.core.api.service.BaseEntitySystemApi;
import it.water.core.api.service.integration.SharedEntityIntegrationClient;

import java.util.Arrays;
import java.util.Collection;


/**
 * @param <T> params indicate a generic water base entity class
 * @Author Aristide Cittadino.
 */
public abstract class OwnedChildBaseEntityServiceImpl<T extends BaseEntity> extends BaseEntityServiceImpl<T> implements BaseEntityApi<T> {
    @Override
    protected final Query createFilterForOwnedOrSharedResource(Query ownedResourceFilter, long loggedUserId) {
        ownedResourceFilter = super.createFilterForOwnedOrSharedResource(ownedResourceFilter, loggedUserId);
        if (OwnedChildResource.class.isAssignableFrom(this.getEntityType())) {
            SharedEntityIntegrationClient sharedEntityIntegrationClient = getSharedEntityIntegrationClient();
            if (sharedEntityIntegrationClient != null) {
                Class<? extends OwnedResource> rootParentClass = getParentResourceClass();
                Collection<Long> entityIds = sharedEntityIntegrationClient.fetchSharingUsersIds(rootParentClass.getName(), loggedUserId);
                if (entityIds != null && !entityIds.isEmpty()) {
                    ownedResourceFilter = ownedResourceFilter.or(getSystemService().getQueryBuilderInstance().field(this.getRootParentFieldPath()).in(Arrays.asList(entityIds.toArray())));
                }
            }
        }
        return ownedResourceFilter;
    }

    /**
     * Constructor for WaterBaseEntityServiceImpl
     *
     * @param type parameter that indicates a generic entity
     */
    protected OwnedChildBaseEntityServiceImpl(Class<T> type) {
        super(type);
    }

    @Override
    protected abstract BaseEntitySystemApi<T> getSystemService();

    protected abstract String getRootParentFieldPath();

    protected abstract Class<? extends OwnedResource> getParentResourceClass();
}
