
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

package it.water.repository.query.order;

import it.water.core.api.repository.query.QueryOrderParameter;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Aristide Cittadino
 */
public class DefaultQueryOrderParameter implements QueryOrderParameter {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private boolean isAsc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultQueryOrderParameter that = (DefaultQueryOrderParameter) o;
        return isAsc == that.isAsc && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isAsc);
    }
}
