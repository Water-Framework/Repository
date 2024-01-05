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
package it.water.repository.service.repository;

import it.water.core.api.model.PaginableResult;
import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.QueryBuilder;
import it.water.core.api.repository.query.QueryOrder;
import it.water.repository.entity.model.PaginatedResult;
import it.water.repository.query.DefaultQueryBuilder;
import it.water.repository.service.api.TestEntityRepository;
import it.water.repository.service.entity.TestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * This class is used just to mock repository operations.
 * It is registered manually from tests
 */
public class TestEntityRepositoryImpl implements TestEntityRepository {
    private static Logger logger = LoggerFactory.getLogger(TestEntityRepositoryImpl.class);

    @Override
    public TestEntity persist(TestEntity entity) {
        return entity;
    }

    @Override
    public TestEntity update(TestEntity entity) {
        return entity;
    }

    @Override
    public void remove(long id) {
        //do nothing
    }

    @Override
    public void remove(TestEntity entity) {
        //do nothing
    }

    @Override
    public void removeAllByIds(Iterable<Long> ids) {
        //do nothing
    }

    @Override
    public void removeAll(Iterable<TestEntity> entities) {
        //do nothing
    }

    @Override
    public void removeAll() {
        //do nothing
    }

    @Override
    public TestEntity find(long id) {
        TestEntity te = new TestEntity();
        te.setId(id);
        return te;
    }

    @Override
    public TestEntity find(Query filter) {
        TestEntity te = new TestEntity();
        te.setId(1);
        return te;
    }

    @Override
    public TestEntity find(String filterStr) {
        TestEntity te = new TestEntity();
        te.setId(1);
        return te;
    }

    @Override
    public long countAll(it.water.core.api.repository.query.Query filter) {
        return 1;
    }

    @Override
    public PaginableResult<TestEntity> findAll(int delta, int page, it.water.core.api.repository.query.Query filter, QueryOrder queryOrder) {
        TestEntity te = new TestEntity();
        te.setId(1);
        PaginatedResult<TestEntity> paginatedResult = new PaginatedResult<>(1, 1, 1, 1, Collections.singleton(te));
        return paginatedResult;
    }

    @Override
    public QueryBuilder getQueryBuilderInstance() {
        return new DefaultQueryBuilder();
    }

}
