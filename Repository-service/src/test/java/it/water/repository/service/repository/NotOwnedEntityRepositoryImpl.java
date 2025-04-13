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
import it.water.repository.service.api.NotOwnedEntityApi;
import it.water.repository.service.api.NotOwnedEntityRepository;
import it.water.repository.service.entity.NotOwnedEntity;
import it.water.repository.service.entity.TestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * This class is used just to mock repository operations.
 * It is registered manually from tests
 */
public class NotOwnedEntityRepositoryImpl implements NotOwnedEntityRepository {
    private static Logger logger = LoggerFactory.getLogger(NotOwnedEntityRepositoryImpl.class);

    @Override
    public Class<NotOwnedEntity> getEntityType() {
        return NotOwnedEntity.class;
    }

    @Override
    public NotOwnedEntity persist(NotOwnedEntity testEntity, Runnable runnable) {
        return persist(testEntity);
    }

    @Override
    public NotOwnedEntity update(NotOwnedEntity testEntity, Runnable runnable) {
        return update(testEntity);
    }

    @Override
    public void remove(long l, Runnable runnable) {
        remove(l);
    }

    @Override
    public NotOwnedEntity persist(NotOwnedEntity entity) {
        return entity;
    }

    @Override
    public NotOwnedEntity update(NotOwnedEntity entity) {
        return entity;
    }

    @Override
    public void remove(long id) {
        //do nothing
    }

    @Override
    public void remove(NotOwnedEntity entity) {
        //do nothing
    }

    @Override
    public void removeAllByIds(Iterable<Long> ids) {
        //do nothing
    }

    @Override
    public void removeAll(Iterable<NotOwnedEntity> entities) {
        //do nothing
    }

    @Override
    public void removeAll() {
        //do nothing
    }

    @Override
    public NotOwnedEntity find(long id) {
        if(id < 0)
            return null;
        NotOwnedEntity te = new NotOwnedEntity();
        te.setId(id);
        return te;
    }

    @Override
    public NotOwnedEntity find(Query filter) {
        NotOwnedEntity te = new NotOwnedEntity();
        te.setId(1);
        return te;
    }

    @Override
    public NotOwnedEntity find(String filterStr) {
        NotOwnedEntity te = new NotOwnedEntity();
        te.setId(1);
        return te;
    }

    @Override
    public long countAll(Query filter) {
        return 1;
    }

    @Override
    public PaginableResult<NotOwnedEntity> findAll(int delta, int page, Query filter, QueryOrder queryOrder) {
        NotOwnedEntity te = new NotOwnedEntity();
        te.setId(1);
        PaginatedResult<NotOwnedEntity> paginatedResult = new PaginatedResult<>(1, 1, 1, 1, Collections.singleton(te));
        return paginatedResult;
    }

    @Override
    public QueryBuilder getQueryBuilderInstance() {
        return new DefaultQueryBuilder();
    }

}
