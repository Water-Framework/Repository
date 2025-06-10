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

import it.water.core.api.entity.owned.OwnedResource;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.service.BaseEntitySystemApi;
import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.core.interceptors.annotations.Inject;
import it.water.repository.service.api.ChildTestEntityApi;
import it.water.repository.service.api.ChildTestEntitySystemApi;
import it.water.repository.service.entity.ChildTestEntity;
import it.water.repository.service.entity.TestEntity;
import lombok.Getter;
import lombok.Setter;

@FrameworkComponent
public class ChildTestEntityServiceImpl extends OwnedChildBaseEntityServiceImpl<ChildTestEntity> implements ChildTestEntityApi {

    @Inject
    @Setter
    @Getter
    private ChildTestEntitySystemApi childTestEntitySystemApi;

    @Inject
    @Setter
    @Getter
    private ComponentRegistry componentRegistry;

    /**
     * Constructor for WaterBaseEntityServiceImpl
     */
    public ChildTestEntityServiceImpl() {
        super(ChildTestEntity.class);
    }

    @Override
    protected String getRootParentFieldPath() {
        return "parent";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    protected BaseEntitySystemApi<ChildTestEntity> getSystemService() {
        return (BaseEntitySystemApi) getChildTestEntitySystemApi();
    }

    @Override
    protected Class<? extends OwnedResource> getParentResourceClass() {
        return TestEntity.class;
    }
}

