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

import it.water.core.api.bundle.ApplicationProperties;
import it.water.core.api.bundle.Runtime;
import it.water.core.api.model.User;
import it.water.core.api.repository.query.Query;
import it.water.core.api.repository.query.QueryOrder;
import it.water.core.api.role.RoleManager;
import it.water.core.api.service.Service;
import it.water.core.api.user.UserManager;
import it.water.core.interceptors.annotations.Inject;
import it.water.core.model.exceptions.ValidationException;
import it.water.core.permission.exceptions.UnauthorizedException;
import it.water.core.registry.model.ComponentConfigurationFactory;
import it.water.core.testing.utils.api.TestPermissionManager;
import it.water.core.testing.utils.bundle.TestRuntimeInitializer;
import it.water.core.testing.utils.junit.WaterTestExtension;
import it.water.core.testing.utils.model.TestHUser;
import it.water.repository.entity.model.exceptions.DuplicateEntityException;
import it.water.repository.entity.model.exceptions.EntityNotFound;
import it.water.repository.service.api.*;
import it.water.repository.service.entity.ChildTestEntity;
import it.water.repository.service.entity.TestEntity;
import it.water.repository.service.entity.TestValidationEntity;
import it.water.repository.service.repository.ChildTestEntityRepositoryImpl;
import it.water.repository.service.repository.TestEntityRepositoryImpl;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, WaterTestExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WaterRepositoryServiceTest implements Service {
    private TestEntityRepositoryImpl workingRepo;
    private ChildTestEntityRepository childWorkingRepo;
    @Inject
    @Setter
    //injecting test permission manager in order to perform some basic security tests
    private TestPermissionManager testPermissionManager;

    @Inject
    @Setter
    private UserManager userManager;

    @Inject
    @Setter
    private RoleManager roleManager;

    @Inject
    @Setter
    private Runtime runtime;

    private User userOk;
    private User userKo;
    private User readUser;

    @BeforeAll
    public void initializeTestFramework() {
        ComponentConfigurationFactory<TestEntityRepositoryImpl> factory = new ComponentConfigurationFactory<>();
        //Mocking test entity repository
        workingRepo = Mockito.mock(TestEntityRepositoryImpl.class);
        childWorkingRepo = Mockito.mock(ChildTestEntityRepositoryImpl.class);
        resetRepositoryMock();
        this.userOk = userManager.addUser("usernameOk", "username", "username", "email@mail.com","Password1_","salt", true);
        this.readUser = userManager.addUser("readUser", "username", "username", "email@mail.com","Password1_","salt", false);
        this.userKo = userManager.addUser("usernameKo", "usernameKo", "usernameKo", "email1@mail.com","Password1_","salt", false);
        roleManager.addRole(this.readUser.getId(),roleManager.getRole(TestEntity.TEST_ENTITY_SAMPLE_ROLE));
        TestRuntimeInitializer.getInstance().getComponentRegistry().registerComponent(TestEntityRepository.class, workingRepo, factory.build());
        TestRuntimeInitializer.getInstance().getComponentRegistry().registerComponent(ChildTestEntityRepository.class, childWorkingRepo, factory.build());
        TestRuntimeInitializer.getInstance().getComponentRegistry().findComponent(TestEntityActionManager.class, null).registerActions(TestEntity.class);
    }

    /**
     * Checking wether all framework components have been initialized correctly
     */
    @Test
    void testRegisteredComponents() {
        Assertions.assertNotNull(TestRuntimeInitializer.getInstance().getComponentRegistry());
        Assertions.assertNotNull(TestRuntimeInitializer.getInstance().getComponentRegistry().findComponents(ApplicationProperties.class, null));
        Assertions.assertNotNull(TestRuntimeInitializer.getInstance().getComponentRegistry().findComponents(TestEntityApi.class, null));
        Assertions.assertNotNull(TestRuntimeInitializer.getInstance().getComponentRegistry().findComponents(TestEntitySystemApi.class, null));
    }

    @Test
    void testEntityMethodSuccess() {
        TestRuntimeInitializer.getInstance().impersonate(userOk,runtime);

        TestHUser user = new TestHUser(1000, "name", "lastname", "email@amil.com", "usernameOk", null, false);
        TestEntity testEntity = new TestEntity();
        testEntity.setId(1);
        testEntity.setEntityField("field");
        testEntity.setUserOwner(user);
        ChildTestEntity childTestEntity = new ChildTestEntity();
        childTestEntity.setParent(testEntity);
        testEntity.getChildren().add(childTestEntity);
        Assertions.assertEquals(1L, getTestEntityApi().save(testEntity).getId());
        testEntity.setEntityField("field1");
        Assertions.assertNotNull(getTestEntityApi().getEntityType());
        Assertions.assertEquals(1L, getTestEntityApi().update(testEntity).getId());
        Assertions.assertNotNull(getChildTestEntityApi());
        Assertions.assertEquals(1L, getTestEntityApi().find(1).getId());
        Assertions.assertEquals(1L, getTestEntityApi().find(workingRepo.getQueryBuilderInstance().field("entityField").equalTo("field1")).getId());
        Assertions.assertEquals(1L, getTestEntityApi().countAll(null));
        Assertions.assertDoesNotThrow(() -> getTestEntityApi().remove(1));
        Assertions.assertEquals(1, getTestEntityApi().findAll(workingRepo.getQueryBuilderInstance().field("entityField").equalTo("field1"), 1, 1, null).getResults().size());
        TestRuntimeInitializer.getInstance().impersonate(readUser,runtime);
        //Bypassing permission and checking only impersonate
        Assertions.assertEquals(1, getTestEntityApi().findAll(workingRepo.getQueryBuilderInstance().field("entityField").equalTo("field1"), 1, 1, null).getResults().size());
        Assertions.assertEquals(1, getChildEntityApi().findAll(null, 1, 1, null).getResults().size());
    }

    @Test
    void testEntityMethodFail() {
        TestRuntimeInitializer.getInstance().impersonate(userKo,runtime);
        TestHUser user = new TestHUser(10001, "name", "lastname", "email@amil.com", "usernameOk", null, false);
        TestEntity testEntity = new TestEntity();
        testEntity.setId(1);
        testEntity.setEntityField("field");
        testEntity.setUserOwner(user);
        TestEntityApi testEntityApi = getTestEntityApi();
        Assertions.assertThrows(UnauthorizedException.class, () -> testEntityApi.save(testEntity));
        TestRuntimeInitializer.getInstance().impersonate(userOk,runtime);
        Mockito.doThrow(DuplicateEntityException.class).when(workingRepo).persist(testEntity);
        Mockito.doThrow(DuplicateEntityException.class).when(workingRepo).update(testEntity);
        Mockito.doReturn(null).when(workingRepo).find(1l);
        TestEntityApi serviceTest = this.getTestEntityApi();
        Assertions.assertThrows(DuplicateEntityException.class, () -> serviceTest.save(testEntity));
        Assertions.assertThrows(DuplicateEntityException.class, () -> serviceTest.update(testEntity));
        Assertions.assertThrows(EntityNotFound.class, () -> serviceTest.remove(1l));
        testEntity.setId(-1);
        Assertions.assertThrows(EntityNotFound.class, () -> serviceTest.update(testEntity));
        //resetting repository mock to invoke real methods
        resetRepositoryMock();
    }

    private TestEntityApi getTestEntityApi() {
        return TestRuntimeInitializer.getInstance().getComponentRegistry().findComponent(TestEntityApi.class, null);
    }

    private ChildTestEntityApi getChildEntityApi() {
        return TestRuntimeInitializer.getInstance().getComponentRegistry().findComponent(ChildTestEntityApi.class, null);
    }

    private ChildTestEntityApi getChildTestEntityApi() {
        return TestRuntimeInitializer.getInstance().getComponentRegistry().findComponent(ChildTestEntityApi.class, null);
    }

    private void resetRepositoryMock() {
        Mockito.doCallRealMethod().when(workingRepo).remove(Mockito.any(Long.class));
        Mockito.doCallRealMethod().when(workingRepo).getQueryBuilderInstance();
        Mockito.doCallRealMethod().when(workingRepo).persist(Mockito.any());
        Mockito.doCallRealMethod().when(workingRepo).update(Mockito.any());
        Mockito.doCallRealMethod().when(workingRepo).countAll(Mockito.any());
        Mockito.doCallRealMethod().when(workingRepo).find(Mockito.any(Long.class));
        Mockito.doCallRealMethod().when(workingRepo).find(Mockito.any(String.class));
        Mockito.doCallRealMethod().when(workingRepo).find(Mockito.any(Query.class));
        Mockito.doCallRealMethod().when(workingRepo).findAll(Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(Query.class), Mockito.nullable(QueryOrder.class));

        Mockito.doCallRealMethod().when(childWorkingRepo).remove(Mockito.any(Long.class));
        Mockito.doCallRealMethod().when(childWorkingRepo).getQueryBuilderInstance();
        Mockito.doCallRealMethod().when(childWorkingRepo).persist(Mockito.any());
        Mockito.doCallRealMethod().when(childWorkingRepo).update(Mockito.any());
        Mockito.doCallRealMethod().when(childWorkingRepo).countAll(Mockito.any());
        Mockito.doCallRealMethod().when(childWorkingRepo).find(Mockito.any(Long.class));
        Mockito.doCallRealMethod().when(childWorkingRepo).find(Mockito.any(String.class));
        Mockito.doCallRealMethod().when(childWorkingRepo).find(Mockito.any(Query.class));
        Mockito.doCallRealMethod().when(childWorkingRepo).findAll(Mockito.nullable(Integer.class), Mockito.nullable(Integer.class), Mockito.nullable(Query.class), Mockito.nullable(QueryOrder.class));
    }

    @Test
    void testValidationSuccess() {
        TestValidationEntity t = new TestValidationEntity();
        t.setId(1);
        t.setEntityField("prova");
        TestValidationEntitySystemApi api = TestRuntimeInitializer.getInstance().getComponentRegistry().findComponent(TestValidationEntitySystemApi.class, null);
        Assertions.assertDoesNotThrow(() -> api.validate(t));
    }

    @Test
    void testValidationFail() {
        TestValidationEntity t = new TestValidationEntity();
        t.setId(1);
        TestValidationEntitySystemApi api = TestRuntimeInitializer.getInstance().getComponentRegistry().findComponent(TestValidationEntitySystemApi.class, null);
        Assertions.assertThrows(ValidationException.class, () -> api.validate(t));
    }
}
