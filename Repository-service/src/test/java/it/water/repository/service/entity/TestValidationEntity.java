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

import it.water.core.api.model.BaseEntity;
import it.water.core.api.model.Resource;
import it.water.core.validation.annotations.NotNullOnPersist;
import it.water.repository.entity.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;

@Access(AccessType.FIELD)
@Entity
public class TestValidationEntity extends AbstractEntity implements BaseEntity {

    @Id
    private long id;

    @Getter
    @Setter
    @NotNullOnPersist
    private String entityField;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}
