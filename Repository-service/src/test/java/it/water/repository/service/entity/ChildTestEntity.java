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
import it.water.core.api.model.EntityExtension;
import it.water.core.api.model.ExpandableEntity;
import it.water.core.permission.action.CrudActions;
import it.water.core.permission.annotations.AccessControl;
import it.water.core.permission.annotations.DefaultRoleAccess;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Access(AccessType.FIELD)
@AccessControl(availableActions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE},
        rolesPermissions = {
                //Admin role can do everything
                @DefaultRoleAccess(roleName = TestEntity.TEST_ENTITY_SAMPLE_ROLE, actions = {CrudActions.SAVE, CrudActions.UPDATE, CrudActions.FIND, CrudActions.FIND_ALL, CrudActions.REMOVE}),
        })
@Data
public class ChildTestEntity implements OwnedChildResource, BaseEntity {
    @Id
    private long id;
    private TestEntity parent;
    private Date entityCreateDate;
    private Date entityModifyDate;
    private Integer entityVersion;
    @Transient
    private Map<String, Object> extraFields = new HashMap<>();
    @Transient
    private EntityExtension extension;

    @ManyToOne
    public TestEntity getParent() {
        return parent;
    }

    @Transient
    public BaseEntity getParentEntity() {
        return parent;
    }

}
