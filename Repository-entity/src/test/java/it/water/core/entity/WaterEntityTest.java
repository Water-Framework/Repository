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
package it.water.core.entity;

import it.water.repository.entity.model.PaginatedResult;
import it.water.repository.entity.model.exceptions.DuplicateEntityException;
import it.water.repository.entity.model.exceptions.EntityNotFound;
import it.water.repository.entity.model.exceptions.NoResultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

class WaterEntityTest {

    @Test
    void testEntityMethods() {
        Date now = new Date();
        WaterTestEntity entity = new WaterTestEntity(1, now);
        Assertions.assertEquals(1, entity.getId());
        Assertions.assertEquals(1, entity.getEntityVersion());
        Assertions.assertEquals(now, entity.getEntityCreateDate());
        Assertions.assertEquals(now, entity.getEntityModifyDate());

    }

    @Test
    void testPaginatedResult() {
        Collection<WaterTestEntity> results = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            WaterTestEntity entity = new WaterTestEntity(1);
            results.add(entity);
        }
        PaginatedResult<WaterTestEntity> paginatedResult = new PaginatedResult<>(5, 1, 2, 10, results);
        Assertions.assertEquals(5, paginatedResult.getNumPages());
        Assertions.assertEquals(1, paginatedResult.getCurrentPage());
        Assertions.assertEquals(2, paginatedResult.getNextPage());
        Assertions.assertEquals(10, paginatedResult.getDelta());
        Assertions.assertTrue(results.containsAll(results));
    }

    @Test
    void testEntityExceptionsModels() {
        String[] fields = {"a", "b"};
        DuplicateEntityException duplicateEntityException = new DuplicateEntityException(fields);
        Assertions.assertEquals("a,b", duplicateEntityException.getMessage());
        EntityNotFound entityNotFound = new EntityNotFound();
        Assertions.assertNotNull(entityNotFound);
        NoResultException noResultException = new NoResultException();
        Assertions.assertNotNull(noResultException);
    }
}
