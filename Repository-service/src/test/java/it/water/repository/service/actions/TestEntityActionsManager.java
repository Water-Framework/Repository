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
package it.water.repository.service.actions;

import it.water.core.api.action.ActionList;
import it.water.core.api.model.Resource;
import it.water.core.interceptors.annotations.FrameworkComponent;
import it.water.core.permission.action.ActionFactory;
import it.water.core.permission.action.DefaultActionsManager;
import it.water.repository.service.api.TestEntityActionManager;
import it.water.repository.service.entity.TestEntity;

import java.util.HashMap;
import java.util.Map;

@FrameworkComponent(services = TestEntityActionManager.class)
public class TestEntityActionsManager extends DefaultActionsManager implements TestEntityActionManager {
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ActionList<? extends Resource>> getActions() {
        Map<String, ActionList<? extends Resource>> actions = new HashMap<>();
        actions.put(TestEntity.class.getName(), ActionFactory.createBaseCrudActionList(TestEntity.class));
        return actions;
    }


}
