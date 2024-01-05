
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

import it.water.core.api.repository.query.QueryOrder;
import it.water.core.api.repository.query.QueryOrderParameter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


/**
 * @Author Aristide Cittadino
 * Interface which implements the concept of order inside a query
 */
public class DefaultQueryOrder implements QueryOrder {

    private Set<QueryOrderParameter> orders;

    public DefaultQueryOrder() {
        orders = new LinkedHashSet<>();
    }

    /**
     * @param name the name of the parameter which is used to order the query's result.
     * @param asc  true if you want to use ascending order.
     */
    public QueryOrder addOrderField(String name, boolean asc) {
        QueryOrderParameter param = new DefaultQueryOrderParameter();
        param.setName(name);
        param.setAsc(asc);
        orders.add(param);
        return this;
    }

    /**
     * @return parameters list.
     */
    public List<QueryOrderParameter> getParametersList() {
        return new ArrayList<>(this.orders);
    }


}
