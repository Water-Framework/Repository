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

package it.water.repository.entity.model;

import it.water.core.api.model.BaseEntity;
import it.water.core.model.AbstractResource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;


/**
 * @Author Aristide Cittadino.
 * This class is a basic implementation of WaterBaseEntity methods.
 */
public abstract class AbstractEntity extends AbstractResource implements BaseEntity {

    /**
     * long id, indicates primary key of entity
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    protected long id;

    /**
     * Version identifier for optimistic locking
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    protected int entityVersion = 1;

    /**
     * Auto filled: create date
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    protected Date entityCreateDate;

    /**
     * Auto filled: update date
     */
    @Getter
    @Setter(AccessLevel.PROTECTED)
    protected Date entityModifyDate;

    @Override
    public String getSystemApiClassName() {
        String className = this.getClass().getName();
        return className.replace(".model.", ".api.") + "SystemApi";
    }

    protected AbstractEntity() {
        initEntity(0, new Date(Instant.now().toEpochMilli()));
    }

    protected AbstractEntity(long id) {
        initEntity(id, new Date(Instant.now().toEpochMilli()));
    }

    protected AbstractEntity(long id, Date entityCreateDate) {
        initEntity(id, entityCreateDate);
    }

    private void initEntity(long id, Date entityCreateDate) {
        this.id = id;
        this.setEntityCreateDate(entityCreateDate);
        this.setEntityModifyDate(entityCreateDate);
        this.setEntityVersion(1);
    }
}
