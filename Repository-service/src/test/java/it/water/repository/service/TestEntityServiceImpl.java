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

import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.service.BaseEntitySystemApi;
import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.core.interceptors.annotations.Inject;
import it.water.repository.service.api.TestEntityApi;
import it.water.repository.service.api.TestEntitySystemApi;
import it.water.repository.service.entity.TestEntity;

@FrameworkComponent
public class TestEntityServiceImpl extends BaseEntityServiceImpl<TestEntity> implements TestEntityApi {

    @Inject
    private TestEntitySystemApi testEntitySystemApi;

    @Inject
    private ComponentRegistry waterComponentRegistry;

    /**
     * Constructor for WaterBaseEntitySystemServiceImpl
     */
    public TestEntityServiceImpl() {
        super(TestEntity.class);
    }

    @Override
    protected BaseEntitySystemApi<TestEntity> getSystemService() {
        return testEntitySystemApi;
    }

    public void setTestEntitySystemApi(TestEntitySystemApi testEntitySystemApi) {
        this.testEntitySystemApi = testEntitySystemApi;
    }

    public void setWaterComponentRegistry(ComponentRegistry waterComponentRegistry) {
        this.waterComponentRegistry = waterComponentRegistry;
    }

    @Override
    protected ComponentRegistry getComponentRegistry() {
        return waterComponentRegistry;
    }

}
