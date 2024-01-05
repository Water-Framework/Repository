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
package it.water.core.repository;

import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.operands.FieldNameOperand;
import it.water.core.api.repository.query.operands.FieldValueOperand;
import it.water.core.api.repository.query.operations.*;
import it.water.repository.query.DefaultQueryBuilder;
import it.water.repository.query.order.DefaultQueryOrder;
import it.water.repository.query.order.DefaultQueryOrderParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RepositoryTest {

    @Test
    void testOrderParameter() {
        DefaultQueryOrder queryOrder = new DefaultQueryOrder();
        queryOrder.addOrderField("a", false);
        DefaultQueryOrderParameter defaultQueryOrderParameter = new DefaultQueryOrderParameter();
        defaultQueryOrderParameter.setAsc(false);
        defaultQueryOrderParameter.setName("a");
        queryOrder.addOrderField("b", false);
        queryOrder.addOrderField("c", false);
        Assertions.assertEquals(3, queryOrder.getParametersList().size());
        Assertions.assertEquals(defaultQueryOrderParameter,queryOrder.getParametersList().get(0));
    }

    @Test
    void testFieldOperand() {
        DefaultQueryBuilder defaultQueryBuilder = new DefaultQueryBuilder();
        FieldNameOperand fieldNameOperand = defaultQueryBuilder.field("prova");
        Assertions.assertEquals("prova = ciao", fieldNameOperand.equalTo("ciao").getDefinition());
        Assertions.assertEquals("prova <> ciao", fieldNameOperand.notEqualTo("ciao").getDefinition());
        Assertions.assertEquals("prova LIKE ciao", fieldNameOperand.like("ciao").getDefinition());
        Assertions.assertEquals("prova >= 30", fieldNameOperand.greaterOrEqualThan(30).getDefinition());
        Assertions.assertEquals("prova <= 30", fieldNameOperand.lowerOrEqualThan(30).getDefinition());
        Assertions.assertEquals("prova > 30", fieldNameOperand.greaterThan(30).getDefinition());
        Assertions.assertEquals("prova < 30", fieldNameOperand.lowerThan(30).getDefinition());
    }

    @Test
    void testOperations() {
        DefaultQueryBuilder defaultQueryBuilder = new DefaultQueryBuilder();

        NotOperation notOperation = new NotOperation();
        notOperation.defineOperands(defaultQueryBuilder.field("uniqueField").equalTo("a"));
        Assertions.assertEquals("NOT (uniqueField = a)", notOperation.getDefinition());


        NotEqualTo notEqualToOperation = new NotEqualTo();
        Assertions.assertEquals("NotEqualTo (!=)", notEqualToOperation.getName());
        notEqualToOperation.defineOperands(defaultQueryBuilder.field("uniqueField"), new FieldValueOperand("a"));
        Assertions.assertEquals("uniqueField <> a", notEqualToOperation.getDefinition());


        EqualTo equalToOperation = new EqualTo();
        Assertions.assertEquals("EqualTo (=)", equalToOperation.getName());
        equalToOperation.defineOperands(defaultQueryBuilder.field("uniqueField"), new FieldValueOperand("a"));
        Assertions.assertEquals("uniqueField = a", equalToOperation.getDefinition());


        LowerThan lowerThanOperation = new LowerThan();
        lowerThanOperation.defineOperands(defaultQueryBuilder.field("uniqueField"), new FieldValueOperand(10));
        Assertions.assertEquals("uniqueField < 10", lowerThanOperation.getDefinition());


        LowerOrEqualThan lowerOrEqualThanOperation = new LowerOrEqualThan();
        lowerOrEqualThanOperation.defineOperands(defaultQueryBuilder.field("uniqueField"), new FieldValueOperand(10));
        Assertions.assertEquals("uniqueField <= 10", lowerOrEqualThanOperation.getDefinition());

        GreaterThan greaterThanOperation = new GreaterThan();
        greaterThanOperation.defineOperands(defaultQueryBuilder.field("uniqueField"), new FieldValueOperand(10));
        Assertions.assertEquals("uniqueField > 10", greaterThanOperation.getDefinition());

        GreaterOrEqualThan greaterOrEqualThanOperation = new GreaterOrEqualThan();
        greaterOrEqualThanOperation.defineOperands(defaultQueryBuilder.field("uniqueField"), new FieldValueOperand(10));
        Assertions.assertEquals("uniqueField >= 10", greaterOrEqualThanOperation.getDefinition());

        Like likeOperation = new Like();
        likeOperation.defineOperands(defaultQueryBuilder.field("uniqueField"), new FieldValueOperand("a"));
        Assertions.assertEquals("uniqueField LIKE a", likeOperation.getDefinition());

        In inOperation = new In();
        inOperation.defineOperands(defaultQueryBuilder.field("uniqueField"), new FieldValueOperand("a"), new FieldValueOperand("b"));
        Assertions.assertEquals("uniqueField IN (a,b)", inOperation.getDefinition());

    }

    @Test
    void testBuilder(){
        DefaultQueryBuilder defaultQueryBuilder = new DefaultQueryBuilder();
        Query q = defaultQueryBuilder.createQueryFilter("a LIKE pippo AND age < 50");
        Query q2 = defaultQueryBuilder.createQueryFilter("a LIKE pippo AND (age < 50 OR name = mario)");
        Query q3 = defaultQueryBuilder.createQueryFilter("a LIKE pippo AND (age < 50 OR name = mario");
        Assertions.assertNotNull(q);
        Assertions.assertNotNull(q2);
        Assertions.assertNull(q3);
    }

}
