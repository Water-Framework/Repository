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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import it.water.core.api.entity.shared.SharedEntity;
import it.water.core.api.model.EntityExtension;
import it.water.core.api.model.ExpandableEntity;
import it.water.core.api.permission.ProtectedEntity;
import it.water.core.permission.action.CrudActions;
import it.water.core.permission.annotations.AccessControl;
import it.water.core.permission.annotations.DefaultRoleAccess;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "uniqueField"), @UniqueConstraint(columnNames = {"combinedUniqueField1", "combinedUniqueField2"})})
@AccessControl(availableActions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE},
        rolesPermissions = {
                //Admin role can do everything
                @DefaultRoleAccess(roleName = TestEntity.TEST_ENTITY_SAMPLE_ROLE, actions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE}),
        })
public class TestEntity implements SharedEntity, ProtectedEntity, ExpandableEntity {
    public static final String TEST_ENTITY_SAMPLE_ROLE = "TEST_ENTITY_SAMPLE_ROLE";
    @Id
    private long id;
    private String entityField;
    @Setter
    @Getter
    private Long ownerUserId;
    private Set<ChildTestEntity> children = new HashSet<>();
    private Date entityCreateDate;
    private Date entityModifyDate;
    private int entityVersion;
    private EntityExtension entityExtension;

    public String getEntityField() {
        return entityField;
    }

    public void setEntityField(String entityField) {
        this.entityField = entityField;
    }


    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    public Set<ChildTestEntity> getChildren() {
        return children;
    }

    public void setChildren(Set<ChildTestEntity> children) {
        this.children = children;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Date getEntityCreateDate() {
        return entityCreateDate;
    }

    public void setEntityCreateDate(Date entityCreateDate) {
        this.entityCreateDate = entityCreateDate;
    }

    @Override
    public Date getEntityModifyDate() {
        return entityModifyDate;
    }

    public void setEntityModifyDate(Date entityModifyDate) {
        this.entityModifyDate = entityModifyDate;
    }

    @Override
    public Integer getEntityVersion() {
        return entityVersion;
    }

    @Override
    public void setEntityVersion(Integer integer) {
        //just for test purpose
    }
    public void setEntityVersion(int entityVersion) {
        this.entityVersion = entityVersion;
    }

    @Override
    public Map<String, Object> getExtraFields() {
        return Map.of();
    }

    @Override
    public void setExtraFields(Map<String, Object> map) {
        // do nothing
    }

    @Override
    public EntityExtension getExtension() {
        return this.entityExtension;
    }

    @Override
    public void setExtension(EntityExtension entityExtension) {
        this.entityExtension = entityExtension;
    }
}
