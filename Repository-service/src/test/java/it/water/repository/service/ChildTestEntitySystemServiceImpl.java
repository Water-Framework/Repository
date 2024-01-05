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

import it.water.core.api.model.Resource;
import it.water.core.api.repository.BaseRepository;
import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.core.interceptors.annotations.Inject;
import it.water.repository.service.api.ChildTestEntityRepository;
import it.water.repository.service.api.ChildTestEntitySystemApi;
import it.water.repository.service.entity.ChildTestEntity;
import lombok.Setter;

@FrameworkComponent
public class ChildTestEntitySystemServiceImpl extends BaseEntitySystemServiceImpl<ChildTestEntity> implements ChildTestEntitySystemApi {

    @Inject
    @Setter
    private ChildTestEntityRepository childTestEntityRepository;

    /**
     * Constructor for WaterBaseEntityServiceImpl
     *
     */
    public ChildTestEntitySystemServiceImpl() {
        super(ChildTestEntity.class);
    }

    @Override
    protected BaseRepository<ChildTestEntity> getRepository() {
        return childTestEntityRepository;
    }

    @Override
    protected void validate(Resource resource) {

    }
}
