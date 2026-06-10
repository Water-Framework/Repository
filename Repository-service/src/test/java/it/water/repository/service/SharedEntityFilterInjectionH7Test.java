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

package it.water.repository.service;

import it.water.core.api.bundle.Runtime;
import it.water.core.api.entity.owned.OwnedResource;
import it.water.core.api.entity.shared.SharedEntity;
import it.water.core.api.model.BaseEntity;
import it.water.core.api.model.PaginableResult;
import it.water.core.api.permission.SecurityContext;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.QueryBuilder;
import it.water.core.api.service.BaseEntitySystemApi;
import it.water.core.api.service.integration.SharedEntityIntegrationClient;
import it.water.repository.query.DefaultQueryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Security regression tests for fix H7 — second-order filter injection via SharedEntity.
 * createFilterForOwnedOrSharedResource must never re-serialize the existing filter: only a
 * literal of server-controlled Long ids is parsed and attached via the programmatic or().
 * Uses the real {@link DefaultQueryBuilder} so the produced Query behaves as in production.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SharedEntityFilterInjectionH7Test {

    @Mock
    private Runtime runtime;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private BaseEntitySystemApi<FakeSharedEntity> systemApi;
    @Mock
    private ComponentRegistry componentRegistry;
    @Mock
    private SharedEntityIntegrationClient sharedEntityIntegrationClient;

    /** Real builder — field()/equalTo()/createQueryFilter()/or() behave as in production. */
    private final QueryBuilder queryBuilder = new DefaultQueryBuilder();

    private FakeSharedEntityServiceImpl serviceUnderTest;

    private static final long LOGGED_USER_ID = 101L;
    private static final String OWNER_FIELD = OwnedResource.getOwnerUserIdFieldName();

    @BeforeEach
    void setUp() {
        serviceUnderTest = new FakeSharedEntityServiceImpl(
                systemApi, componentRegistry, runtime, sharedEntityIntegrationClient);
        Mockito.when(runtime.getSecurityContext()).thenReturn(securityContext);
        Mockito.when(securityContext.isAdmin()).thenReturn(false);
        Mockito.when(securityContext.getLoggedEntityId()).thenReturn(LOGGED_USER_ID);
        Mockito.when(systemApi.getQueryBuilderInstance()).thenReturn(queryBuilder);
    }

    private Query captureAppliedFilter(BaseEntitySystemApi<?> api) {
        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        Mockito.verify(api).findAll(captor.capture(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any());
        return captor.getValue();
    }

    // H7-1: no shared ids -> only the ownerUserId = filter is applied (no OR / IN)
    @Test
    void testH7NoSharedIdsOnlyOwnerFilterApplied() {
        Mockito.when(sharedEntityIntegrationClient.fetchSharingUsersIds(
                FakeSharedEntity.class.getName(), LOGGED_USER_ID)).thenReturn(Collections.emptyList());
        Mockito.when(systemApi.findAll(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(emptyPage());

        serviceUnderTest.findAll(null, 10, 1, null);

        Query applied = captureAppliedFilter(systemApi);
        Assertions.assertNotNull(applied, "An OwnedResource must always be scoped by owner");
        String def = applied.getDefinition();
        Assertions.assertTrue(def.contains(OWNER_FIELD), "Owner condition expected: " + def);
        Assertions.assertTrue(def.contains(String.valueOf(LOGGED_USER_ID)), "Logged user id expected: " + def);
        Assertions.assertFalse(def.toUpperCase().contains("IN ("), "No IN clause when no shared ids: " + def);
        Assertions.assertFalse(def.toUpperCase().contains("OR"), "No OR clause when no shared ids: " + def);
    }

    // H7-2: legitimate shared ids -> owner OR id IN (ids)
    @Test
    void testH7LegitimateSharedIdsFilterExtendedWithOrIn() {
        Mockito.when(sharedEntityIntegrationClient.fetchSharingUsersIds(
                FakeSharedEntity.class.getName(), LOGGED_USER_ID)).thenReturn(List.of(200L, 300L));
        Mockito.when(systemApi.findAll(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(emptyPage());

        serviceUnderTest.findAll(null, 10, 1, null);

        String def = captureAppliedFilter(systemApi).getDefinition().toUpperCase();
        Assertions.assertTrue(def.contains(OWNER_FIELD.toUpperCase()), "Owner condition expected: " + def);
        Assertions.assertTrue(def.contains("OR"), "OR clause expected: " + def);
        Assertions.assertTrue(def.contains("IN ("), "IN clause expected: " + def);
        Assertions.assertTrue(def.contains("200") && def.contains("300"), "Both shared ids expected: " + def);
    }

    // H7-3: the IN literal contains ONLY numeric ids and there is exactly ONE OR (the ownership one).
    // A reversion to concatenating non-numeric / attacker-influenced data would break these assertions.
    @Test
    void testH7SharedIdsInClauseIsPurelyNumericNoInjectedOperators() {
        Mockito.when(sharedEntityIntegrationClient.fetchSharingUsersIds(
                FakeSharedEntity.class.getName(), LOGGED_USER_ID)).thenReturn(List.of(10L, 20L, 30L));
        Mockito.when(systemApi.findAll(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(emptyPage());

        serviceUnderTest.findAll(null, 10, 1, null);

        String def = captureAppliedFilter(systemApi).getDefinition();

        // Extract the content between "IN (" and the following ")"
        String up = def.toUpperCase();
        int inStart = up.indexOf("IN (");
        Assertions.assertTrue(inStart >= 0, "IN clause expected: " + def);
        int open = def.indexOf('(', inStart);
        int close = def.indexOf(')', open);
        Assertions.assertTrue(close > open, "Closed IN parenthesis expected: " + def);
        String inContent = def.substring(open + 1, close);
        Assertions.assertTrue(inContent.matches("[0-9,\\s]+"),
                "IN clause must contain only numeric ids (no injected metacharacters): '" + inContent + "'");

        // Exactly one OR (the ownership OR) — no injected boolean operators
        int orCount = up.split("\\bOR\\b", -1).length - 1;
        Assertions.assertEquals(1, orCount, "Exactly one OR (the ownership clause) expected: " + def);
    }

    // H7-4: empty shared id list behaves like no shared ids (no IN clause)
    @Test
    void testH7EmptySharedIdListNoInClauseAdded() {
        Mockito.when(sharedEntityIntegrationClient.fetchSharingUsersIds(
                Mockito.any(), Mockito.anyLong())).thenReturn(Collections.emptyList());
        Mockito.when(systemApi.findAll(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(emptyPage());

        serviceUnderTest.findAll(null, 5, 1, null);

        String def = captureAppliedFilter(systemApi).getDefinition().toUpperCase();
        Assertions.assertFalse(def.contains("IN ("), "No IN clause with empty shared ids: " + def);
        Assertions.assertFalse(def.contains("OR"), "No OR clause with empty shared ids: " + def);
    }

    // H7-5: non-OwnedResource type -> no ownership filter, no SharedEntity client call
    @Test
    void testH7NonOwnedEntityTypeNoOwnerFilterApplied() {
        @SuppressWarnings("unchecked")
        BaseEntitySystemApi<FakePlainEntity> plainSystemApi = Mockito.mock(BaseEntitySystemApi.class);
        FakePlainEntityServiceImpl plainService = new FakePlainEntityServiceImpl(
                plainSystemApi, componentRegistry, runtime, sharedEntityIntegrationClient);
        Mockito.when(plainSystemApi.findAll(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(emptyPagePlain());

        plainService.findAll(null, 10, 1, null);

        Mockito.verify(sharedEntityIntegrationClient, Mockito.never())
                .fetchSharingUsersIds(Mockito.any(), Mockito.anyLong());
        Assertions.assertNull(captureAppliedFilter(plainSystemApi),
                "No ownership filter for a non-OwnedResource entity type");
    }

    // ----------------------------------------------------------------- helpers / fixtures

    @SuppressWarnings("unchecked")
    private static PaginableResult<FakeSharedEntity> emptyPage() {
        return Mockito.mock(PaginableResult.class);
    }

    @SuppressWarnings("unchecked")
    private static PaginableResult<FakePlainEntity> emptyPagePlain() {
        return Mockito.mock(PaginableResult.class);
    }

    /** Entity that is both OwnedResource and SharedEntity — the H7 target type. */
    static final class FakeSharedEntity implements SharedEntity {
        private long id;
        private Long ownerUserId;
        @Override public long getId() { return id; }
        @Override public Long getOwnerUserId() { return ownerUserId; }
        @Override public void setOwnerUserId(Long userId) { this.ownerUserId = userId; }
        @Override public Date getEntityCreateDate() { return null; }
        @Override public Date getEntityModifyDate() { return null; }
        @Override public Integer getEntityVersion() { return 1; }
        @Override public void setEntityVersion(Integer version) { /* stub */ }
    }

    /** Plain entity: neither OwnedResource nor SharedEntity. */
    static final class FakePlainEntity implements BaseEntity {
        @Override public long getId() { return 0; }
        @Override public Date getEntityCreateDate() { return null; }
        @Override public Date getEntityModifyDate() { return null; }
        @Override public Integer getEntityVersion() { return 1; }
        @Override public void setEntityVersion(Integer version) { /* stub */ }
    }

    static final class FakeSharedEntityServiceImpl extends BaseEntityServiceImpl<FakeSharedEntity> {
        private final BaseEntitySystemApi<FakeSharedEntity> sysApi;
        private final ComponentRegistry registry;
        private final SharedEntityIntegrationClient sharedClient;

        FakeSharedEntityServiceImpl(BaseEntitySystemApi<FakeSharedEntity> sysApi, ComponentRegistry registry,
                                    Runtime runtime, SharedEntityIntegrationClient sharedClient) {
            super(FakeSharedEntity.class);
            this.sysApi = sysApi;
            this.registry = registry;
            this.sharedClient = sharedClient;
            this.setRuntime(runtime);
        }

        @Override protected BaseEntitySystemApi<FakeSharedEntity> getSystemService() { return sysApi; }
        @Override protected ComponentRegistry getComponentRegistry() { return registry; }
        @Override protected SharedEntityIntegrationClient getSharedEntityIntegrationClient() { return sharedClient; }
    }

    static final class FakePlainEntityServiceImpl extends BaseEntityServiceImpl<FakePlainEntity> {
        private final BaseEntitySystemApi<FakePlainEntity> sysApi;
        private final ComponentRegistry registry;
        private final SharedEntityIntegrationClient sharedClient;

        FakePlainEntityServiceImpl(BaseEntitySystemApi<FakePlainEntity> sysApi, ComponentRegistry registry,
                                   Runtime runtime, SharedEntityIntegrationClient sharedClient) {
            super(FakePlainEntity.class);
            this.sysApi = sysApi;
            this.registry = registry;
            this.sharedClient = sharedClient;
            this.setRuntime(runtime);
        }

        @Override protected BaseEntitySystemApi<FakePlainEntity> getSystemService() { return sysApi; }
        @Override protected ComponentRegistry getComponentRegistry() { return registry; }
        @Override protected SharedEntityIntegrationClient getSharedEntityIntegrationClient() { return sharedClient; }
    }
}
