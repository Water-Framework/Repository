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

import it.water.core.api.entity.owned.OwnedChildResource;
import it.water.core.api.model.BaseEntity;
import it.water.repository.service.api.ChildTestEntitySystemApi;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import java.util.Date;

@Entity
public class ChildTestEntity implements OwnedChildResource, BaseEntity {
    @Id
    private long id;
    private TestEntity parent;
    private Date entityCreateDate;
    private Date entityModifyDate;
    private int entityVersion;

    public void setParent(TestEntity parent) {
        this.parent = parent;
    }

    @ManyToOne
    public TestEntity getParent() {
        return parent;
    }

    @Transient
    public BaseEntity getParentEntity() {
        return parent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getEntityCreateDate() {
        return entityCreateDate;
    }

    public void setEntityCreateDate(Date entityCreateDate) {
        this.entityCreateDate = entityCreateDate;
    }

    public Date getEntityModifyDate() {
        return entityModifyDate;
    }

    public void setEntityModifyDate(Date entityModifyDate) {
        this.entityModifyDate = entityModifyDate;
    }

    public Integer getEntityVersion() {
        return entityVersion;
    }

    @Override
    public void setEntityVersion(Integer integer) {

    }

    public void setEntityVersion(int entityVersion) {
        this.entityVersion = entityVersion;
    }
}
