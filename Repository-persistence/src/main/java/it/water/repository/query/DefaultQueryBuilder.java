
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

package it.water.repository.query;

import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.operands.FieldNameOperand;
import it.water.repository.query.parser.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Author Aristide Cittadino.
 * Utility class to create a query object starting from a filter as a string.
 * es. (age > 20 AND name like 'pippo') OR (....)
 */
public class DefaultQueryBuilder implements it.water.core.api.repository.query.QueryBuilder {
    private static Logger log = LoggerFactory.getLogger(DefaultQueryBuilder.class);

    public Query createQueryFilter(String filter) {
        try {
            QueryParser parser = new QueryParser(filter);
            return parser.parse();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public FieldNameOperand field(String name) {
        return new FieldNameOperand(name);
    }
}
