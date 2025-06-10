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
package it.water.repository.service.entity;

import java.util.Date;

import it.water.core.api.model.BaseEntity;
import it.water.core.api.model.EntityExtension;
import it.water.core.api.permission.ProtectedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TestEntityExtension implements ProtectedEntity, EntityExtension {
    public static final String TEST_ENTITY_SAMPLE_ROLE = "TEST_ENTITY_SAMPLE_ROLE";
    @Id
    private long id;
    private String entityField;

    public String getEntityField() {
        return entityField;
    }

    public void setEntityField(String entityField) {
        this.entityField = entityField;
    }


    @Override
    public void setupExtensionFields(long l, BaseEntity baseEntity) {
        // do nothing
    }

    @Override
    public long getRelatedEntityId() {
        return 0;
    }

    @Override
    public void setRelatedEntityId(long l) {
        //do nothing
    }

    @Override
    public long getId() {
        return 0;
    }

    @Override
    public Date getEntityCreateDate() {
        return null;
    }

    @Override
    public Date getEntityModifyDate() {
        return null;
    }

    @Override
    public Integer getEntityVersion() {
        return 0;
    }

    @Override
    public void setEntityVersion(Integer integer) {
        //do nothing
    }
}
