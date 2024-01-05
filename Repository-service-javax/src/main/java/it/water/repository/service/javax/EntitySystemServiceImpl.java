
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

package it.water.repository.service.javax;

import it.water.core.api.model.BaseEntity;
import it.water.core.api.model.Resource;
import it.water.core.validation.javax.validators.WaterJavaxValidator;
import it.water.repository.service.BaseEntitySystemServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <T>
 * @Author Aristide Cittadino
 */
public abstract class EntitySystemServiceImpl<T extends BaseEntity> extends BaseEntitySystemServiceImpl<T> {
    private static Logger log = LoggerFactory.getLogger(EntitySystemServiceImpl.class);
    private static WaterJavaxValidator validator = new WaterJavaxValidator();

    protected EntitySystemServiceImpl(Class<T> type) {
        super(type);
    }

    @Override
    protected void validate(Resource entity) {
        log.debug("Validating entity {}", entity);
        validator.validate(entity);
    }

}
