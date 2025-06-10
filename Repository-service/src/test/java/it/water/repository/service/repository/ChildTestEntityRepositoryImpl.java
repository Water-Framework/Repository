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
import it.water.repository.service.api.ChildTestEntityRepository;
import it.water.repository.service.entity.ChildTestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * This class is used just to mock repository operations.
 * It is registered manually from tests
 */
public class ChildTestEntityRepositoryImpl implements ChildTestEntityRepository {
    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(ChildTestEntityRepositoryImpl.class);

    @Override
    public Class<ChildTestEntity> getEntityType() {
        return ChildTestEntity.class;
    }

    @Override
    public ChildTestEntity persist(ChildTestEntity childTestEntity, Runnable runnable) {
        return persist(childTestEntity);
    }

    @Override
    public ChildTestEntity update(ChildTestEntity childTestEntity, Runnable runnable) {
        return update(childTestEntity);
    }

    @Override
    public void remove(long l, Runnable runnable) {
        remove(l);
    }

    @Override
    public ChildTestEntity persist(ChildTestEntity entity) {
        return entity;
    }

    @Override
    public ChildTestEntity update(ChildTestEntity entity) {
        return entity;
    }

    @Override
    public void remove(long id) {
        //do nothing
    }

    @Override
    public void remove(ChildTestEntity entity) {
        //do nothing
    }

    @Override
    public void removeAllByIds(Iterable<Long> ids) {
        //do nothing
    }

    @Override
    public void removeAll(Iterable<ChildTestEntity> entities) {
        //do nothing
    }

    @Override
    public void removeAll() {
        //do nothing
    }

    @Override
    public ChildTestEntity find(long id) {
        ChildTestEntity te = new ChildTestEntity();
        te.setId(id);
        return te;
    }

    @Override
    public ChildTestEntity find(Query filter) {
        ChildTestEntity te = new ChildTestEntity();
        te.setId(1);
        return te;
    }

    @Override
    public ChildTestEntity find(String filterStr) {
        ChildTestEntity te = new ChildTestEntity();
        te.setId(1);
        return te;
    }

    @Override
    public long countAll(Query filter) {
        return 1;
    }

    @Override
    public PaginableResult<ChildTestEntity> findAll(int delta, int page, Query filter, QueryOrder queryOrder) {
        ChildTestEntity te = new ChildTestEntity();
        te.setId(1);
        return new PaginatedResult<>(1, 1, 1, 1, Collections.singleton(te));
    }


    @Override
    public QueryBuilder getQueryBuilderInstance() {
        return new DefaultQueryBuilder();
    }

}
