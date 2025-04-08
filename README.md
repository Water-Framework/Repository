# Repository

## Overview

The Water Repository project is a foundational component designed to manage data entities within the Water ecosystem. It provides a flexible and extensible framework for storing, retrieving, and managing data. This repository offers persistence, service, and entity management capabilities, abstracting the underlying data storage mechanism and providing a consistent API for data access. It includes features for querying, ordering, and paginating entities, handling entity validation, and producing events on entity lifecycle changes. The repository also integrates security aspects, including ownership and sharing of resources, to ensure data is accessed and manipulated in a secure and controlled manner.

The project is intended for developers building applications within the Water ecosystem who need a robust and well-structured data management solution. It simplifies data access, enforces consistency, and provides a foundation for building complex data-driven applications.

The specific purpose of this repository is to provide a set of core modules that handle entity definition, persistence, and service layers. Each module plays a specific role:

*   **Repository-entity:** Defines the base entity model and common exceptions.
*   **Repository-persistence:** Provides query building and parsing capabilities.
*   **Repository-service:** Implements the service layer with CRUD operations, validation, event production, and security integration.

## Technology Stack

*   **Language:** Java
*   **Build Tool:** Gradle
*   **Logging:** SLF4J
*   **Code Reduction:** Lombok
*   **JSON Processing:** Jackson
*   **Core Library:** it.water.core (APIs, models, registry, interceptors, services, security, permission, bundle, and testing utilities)
*   **Testing:** JUnit Jupiter, Mockito
*   **Class Indexing:** org.atteo.classindex
*   **ORM (Testing):** Hibernate
*   **Persistence API:** Jakarta Persistence API
*   **In-Memory Database (Testing):** HSQLDB
*   **Bean Validation:** Jakarta Validation API
*   **Maven Publishing:** Gradle Maven Publish Plugin
*   **Code Coverage:** Jacoco
*   **Code Quality Analysis:** SonarQube

## Directory Structure

```
Repository/
├── build.gradle                  - Root build file for the entire project.
├── gradle.properties             - Gradle properties file.
├── settings.gradle               - Settings file defining subprojects.
├── Repository-entity/            - Module defining the entity model.
│   ├── build.gradle              - Build file for the entity module.
│   ├── src/
│   │   ├── main/
│   │   │   └── java/it/water/repository/entity/model/
│   │   │       ├── AbstractEntity.java       - Abstract base class for entities.
│   │   │       ├── PaginatedResult.java      - Class representing paginated results.
│   │   │       └── exceptions/              - Package containing custom exceptions.
│   │   │           ├── DuplicateEntityException.java
│   │   │           ├── EntityNotFound.java
│   │   │           └── NoResultException.java
│   │   └── test/
│   │       └── java/it/water/core/entity/
│   │           ├── WaterEntityTest.java      - Test class for entity methods.
│   │           └── WaterTestEntity.java      - Test entity class.
├── Repository-persistence/       - Module for query building and persistence logic.
│   ├── build.gradle              - Build file for the persistence module.
│   ├── src/
│   │   ├── main/
│   │   │   └── java/it/water/repository/query/
│   │   │       ├── DefaultQueryBuilder.java   - Class for building queries.
│   │   │       ├── order/                  - Package for query ordering.
│   │   │       │   ├── DefaultQueryOrder.java    - Class for defining query order.
│   │   │       │   └── DefaultQueryOrderParameter.java - Class representing an order parameter.
│   │   │       └── parser/                 - Package for query parsing.
│   │   │           └── QueryParser.java       - Class for parsing query strings.
│   │   └── test/
│   │       └── java/it/water/core/repository/
│   │           └── RepositoryTest.java       - Test class for repository functionality.
├── Repository-service/           - Module implementing the service layer.
│   ├── build.gradle              - Build file for the service module.
│   ├── src/
│   │   ├── main/
│   │   │   └── java/it/water/repository/service/
│   │   │       ├── BaseEntityServiceImpl.java    - Base service implementation for entities.
│   │   │       ├── BaseEntitySystemServiceImpl.java - Base service implementation for system-level operations.
│   │   │       └── OwnedChildBaseEntityServiceImpl.java - Base service implementation for child entities.
│   │   └── test/
│   │       └── java/it/water/repository/service/
│   │           ├── api/                      - Package for API interfaces.
│   │           │   ├── ChildTestEntityApi.java
│   │           │   ├── ChildTestEntityRepository.java
│   │           │   ├── ChildTestEntitySystemApi.java
│   │           │   ├── TestEntityActionManager.java
│   │           │   ├── TestEntityApi.java
│   │           │   ├── TestEntityRepository.java
│   │           │   ├── TestEntitySystemApi.java
│   │           │   └── TestValidationEntitySystemApi.java
│   │           ├── entity/                   - Package for entity classes used in testing.
│   │           │   ├── ChildTestEntity.java
│   │           │   ├── TestEntity.java
│   │           │   └── TestValidationEntity.java
│   │           ├── repository/               - Package for repository implementations used in testing.
│   │           │   ├── ChildTestEntityRepositoryImpl.java
│   │           │   ├── HibernateUtil.java
│   │           │   └── TestEntityRepositoryImpl.java
│   │           ├── actions/                  - Package for action managers.
│   │           │   └── TestEntityActionsManager.java
│   │           ├── ChildTestEntityServiceImpl.java
│   │           ├── ChildTestEntitySystemServiceImpl.java
│   │           ├── TestEntityServiceImpl.java
│   │           ├── TestEntitySystemServiceImpl.java
│   │           ├── TestValidationEntitySystemServiceImpl.java
│   │           └── WaterRepositoryServiceTest.java - Test class for service functionality.
└── README.md                     - Project documentation.

```

## Getting Started

### Prerequisites

*   **Java:** Version 8 or higher.
*   **Gradle:** Version 6.0 or higher.
*   **it.water.core:** Ensure the `it.water.core` library is available in your Maven local repository or a configured Maven repository. This library is a core dependency for the project.

### Build Steps

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/Water-Framework/Repository.git
    cd Repository
    ```

2.  **Compile the code:**

    ```bash
    gradle build
    ```

3.  **Run tests:**

    ```bash
    gradle test
    ```

4.  **Generate JaCoCo coverage report:**

    ```bash
    gradle jacocoRootReport
    ```

5.  **Publish to Maven Local (optional):**

    ```bash
    gradle publishToMavenLocal
    ```
### Configuration

The project uses system properties for configuring the Maven repository. Ensure the following properties are set:

*   `publishRepoUsername`: Username for the Maven repository.
*   `publishRepoPassword`: Password for the Maven repository.
*   `sonar.host.url`: URL of the SonarQube server.
*   `sonar.login`: Login token for SonarQube.

These properties can be set as system environment variables or passed directly to the Gradle command:

```bash
gradle build -DpublishRepoUsername=your_username -DpublishRepoPassword=your_password -Dsonar.host.url=your_sonar_url -Dsonar.login=your_sonar_token
```
### Module Usage

#### Repository-entity

This module defines the base entity model for the repository. It includes the `AbstractEntity` class, which serves as the base class for all persistent entities. To use this module in your project, add it as a dependency in your `build.gradle` file:

```gradle
dependencies {
    implementation group: 'it.water.repository', name: 'Repository-entity', version: project.waterVersion
}
```

Then, create your entities by extending the `AbstractEntity` class:

```java
import it.water.repository.entity.model.AbstractEntity;

public class MyEntity extends AbstractEntity {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

This module also provides the `PaginatedResult` class for representing paginated query results and a set of custom exceptions for handling common entity-related errors.

#### Repository-persistence

This module provides query building and parsing capabilities. It includes the `DefaultQueryBuilder` class for constructing queries and the `QueryParser` class for parsing query strings. To use this module, add it as a dependency:

```gradle
dependencies {
    implementation group: 'it.water.repository', name: 'Repository-persistence', version: project.waterVersion
}
```

You can then use the `DefaultQueryBuilder` to create queries programmatically:

```java
import it.water.repository.query.DefaultQueryBuilder;
import it.water.core.api.query.Query;

public class MyQueryBuilder {
    public Query createMyQuery(String name) {
        DefaultQueryBuilder queryBuilder = new DefaultQueryBuilder();
        return queryBuilder.createQueryFilter("name == '" + name + "'");
    }
}
```

This module allows you to define complex query conditions and ordering using a fluent API.

#### Repository-service

This module implements the service layer for the repository. It includes the `BaseEntityServiceImpl` and `BaseEntitySystemServiceImpl` classes, which provide common CRUD operations and methods for querying, paginating, and securing entities. To use this module, add it as a dependency:

```gradle
dependencies {
    implementation project(":Repository-entity")
    implementation project(":Repository-persistence")
    implementation group: 'it.water.repository', name: 'Repository-service', version: project.waterVersion
}
```

You can then create your service implementations by extending the base service classes:

```java
import it.water.repository.service.BaseEntityServiceImpl;
import it.water.repository.entity.model.MyEntity;
import it.water.core.api.query.Query;

public class MyEntityServiceImpl extends BaseEntityServiceImpl<MyEntity> {

    @Override
    public MyEntity find(long id) {
        // Implement your find logic here
        return null;
    }

    @Override
    public MyEntity find(Query query) {
        // Implement your find logic here
        return null;
    }
}
```

This module provides a foundation for building a robust and scalable service layer for your entities.

**Example Usage:**

To illustrate how these modules can be used in a real-world scenario, consider a simple application that manages `User` entities. First, define the `User` entity in a separate module (e.g., `MyApplication-entity`) by extending `AbstractEntity` from `Repository-entity`:

```java
// MyApplication-entity/src/main/java/com/example/myapp/entity/User.java
package com.example.myapp.entity;

import it.water.repository.entity.model.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class User extends AbstractEntity {
    private String username;
    private String email;

    public User(){}

    public User(long id, Date creationDate, String username, String email){
        super();
        initEntity(id, creationDate);
        this.username = username;
        this.email = email;
    }
}
```

Next, create a service interface and implementation (e.g., `MyApplication-service`) that uses `Repository-service` and `Repository-persistence`:

```java
// MyApplication-service/src/main/java/com/example/myapp/service/UserService.java
package com.example.myapp.service;

import com.example.myapp.entity.User;
import it.water.core.api.query.Query;
import it.water.core.api.service.BaseEntityApi;

public interface UserService extends BaseEntityApi<User> {
    User findByUsername(String username);
    User findByQuery(Query query);
}
```

```java
// MyApplication-service/src/main/java/com/example/myapp/service/UserServiceImpl.java
package com.example.myapp.service;

import com.example.myapp.entity.User;
import it.water.repository.service.BaseEntityServiceImpl;
import it.water.core.api.query.Query;
import org.springframework.stereotype.Service; // Assuming Spring or similar DI framework

@Service
public class UserServiceImpl extends BaseEntityServiceImpl<User> implements UserService {

    @Override
    public User findByUsername(String username) {
        // Use DefaultQueryBuilder from Repository-persistence to build the query
        Query query = getQueryBuilderInstance().createQueryFilter("username == '" + username + "'");
        return find(query);
    }

    @Override
    public User find(long id) {
        return null;
    }

    @Override
    public User find(Query query) {
        return null;
    }
}
```

In this example, `UserServiceImpl` extends `BaseEntityServiceImpl` from `Repository-service` and uses the `DefaultQueryBuilder` (available via `getQueryBuilderInstance()`) to dynamically create a query to find a user by username.  The concrete implementation for the find methods is omitted for brevity but would involve interacting with a repository (DAO) to retrieve the data from a database.  This demonstrates how the modules can be combined to create a data management solution for a specific application.

## Functional Analysis

### 1. Main Responsibilities of the System

The primary responsibilities of the Water Repository project are:

*   **Entity Management:** Providing a base entity class (`AbstractEntity`) and related interfaces for defining and managing data entities.
*   **Persistence Abstraction:** Abstracting the underlying data storage mechanism through the repository pattern, allowing developers to switch between different databases or storage solutions without modifying the service layer.
*   **CRUD Operations:** Implementing Create, Read, Update, and Delete (CRUD) operations for entities through the service layer.
*   **Querying and Filtering:** Providing a flexible query building and parsing mechanism for retrieving entities based on specific criteria.
*   **Pagination:** Supporting pagination of query results to efficiently handle large datasets.
*   **Validation:** Providing a validation mechanism to ensure data integrity.
*   **Event Production:** Producing events on entity lifecycle changes (creation, update, deletion) to enable other parts of the system to react to these changes.
*   **Security Integration:** Integrating security aspects, such as ownership and sharing of resources, into the data access layer.

### 2. Problems the System Solves

The Water Repository project solves the following problems:

*   **Data Access Complexity:** It simplifies data access by providing a consistent API for interacting with entities, abstracting the complexities of the underlying data storage mechanism.
*   **Code Duplication:** It reduces code duplication by providing base classes and interfaces that encapsulate common entity management logic.
*   **Lack of Standardization:** It provides a standardized way to manage entities within the Water ecosystem, ensuring consistency across different applications.
*   **Tight Coupling:** It promotes loose coupling between the service layer and the data access layer through the repository pattern.
*   **Inflexible Querying:** It provides a flexible query building and parsing mechanism that allows developers to retrieve entities based on complex criteria.
*   **Security Vulnerabilities:** It integrates security aspects into the data access layer to prevent unauthorized access and manipulation of data.

### 3. Interaction of Modules and Components

The modules and components interact with each other as follows:

*   **Repository-entity:** Defines the base entity model, which is used by the other modules.
*   **Repository-persistence:** Provides query building and parsing capabilities, which are used by the service layer to construct and interpret queries.
*   **Repository-service:** Implements the service layer, which uses the entity model from `Repository-entity` and the query building and parsing capabilities from `Repository-persistence` to manage entities.

The `BaseEntityServiceImpl` and `BaseEntitySystemServiceImpl` classes handle the core business logic for entities. The "System" services often handle validation, authorization, and event publication, while regular services handle data access and manipulation. Services delegate persistence operations (CRUD) to the repositories (`TestEntityRepositoryImpl`, `ChildTestEntityRepositoryImpl`). The `DefaultQueryBuilder` and `QueryParser` are used to construct and interpret queries, enabling flexible data retrieval. `OwnedChildBaseEntityServiceImpl` and the `ChildTestEntity` demonstrate parent-child relationships between entities, enabling cascading operations and data filtering based on ownership. The `BaseEntitySystemServiceImpl` uses a `WaterValidator` to validate entities before persistence and produces events upon entity creation, update, or deletion. `BaseEntityServiceImpl` uses a `SecurityContext` to create conditions for owned or shared resources, integrating security into the data access layer.

### 4. User-Facing vs. System-Facing Functionalities

The Water Repository project primarily provides system-facing functionalities. It is a framework for building data management solutions, rather than a user-facing application.

*   **System-Facing Functionalities:**
    *   Entity management APIs (CRUD operations, querying, pagination).
    *   Validation mechanism.
    *   Event production.
    *   Security integration.

These functionalities are intended for use by other system components, such as services, applications, and frameworks within the Water ecosystem.

The project does not provide any direct user-facing functionalities, such as UIs, REST endpoints, or CLI commands. These would be implemented by applications that use the Water Repository project as a dependency.

## Architectural Patterns and Design Principles Applied

*   **Layered Architecture:** The project is divided into distinct layers (entity, persistence, service), each with a specific responsibility. This promotes separation of concerns and improves maintainability.
*   **Repository Pattern:** The use of repositories (`TestEntityRepositoryImpl`, `ChildTestEntityRepositoryImpl`) provides an abstraction layer between the service layer and the data access layer. This allows to change the underlying data storage mechanism without affecting the service layer.
*   **Service Layer:** The service layer (`BaseEntityServiceImpl`, `BaseEntitySystemServiceImpl`) encapsulates the business logic and provides a clean API for interacting with entities.
*   **Data Transfer Object (DTO):** While not explicitly defined, the entity classes (`TestEntity`, `ChildTestEntity`) can be considered DTOs, as they are used to transfer data between layers.
*   **Dependency Injection:** The use of component registries and the `@Inject` annotation (inferred from the context) suggests the use of dependency injection, which promotes loose coupling and testability.
*   **Template Method Pattern:** The base service implementations (`BaseEntityServiceImpl`, `BaseEntitySystemServiceImpl`, `OwnedChildBaseEntityServiceImpl`) likely use the template method pattern, providing a common algorithm for entity management while allowing subclasses to override specific steps.
*   **SOLID Principles:**
    *   **Single Responsibility Principle:** Each class has a specific responsibility.
    *   **Open/Closed Principle:** The use of inheritance and interfaces allows to extend the functionality of the system without modifying existing code.
    *   **Liskov Substitution Principle:** Subclasses of `AbstractEntity` can be used interchangeably with their base class.
    *   **Interface Segregation Principle:** The API interfaces (`TestEntityApi`, `ChildTestEntityApi`, `TestEntitySystemApi`, `ChildTestEntitySystemApi`) are specific to the needs of the clients.
    *   **Dependency Inversion Principle:** The service layer depends on abstractions (repositories, interfaces) rather than concrete implementations.
*   **Interceptor Pattern:** The `Core-interceptors` dependency suggests the use of interceptors for cross-cutting concerns like logging, security, or validation. These interceptors can be applied to service methods to add behavior without modifying the core logic.
*   **Event-Driven Architecture:** The system produces events upon entity creation, update, or deletion. This allows other modules or systems to react to these events in a loosely coupled manner.
*   **Role-Based Access Control (RBAC):** The inclusion of `Core-security` and `Core-permission` suggests the implementation of RBAC to control access to entities and operations based on user roles and permissions.

## Common Annotations or Behaviors Applied via Interface or Base Class

*   **AbstractEntity:** This abstract class applies common fields (`id`, `entityVersion`, `entityCreateDate`, `entityModifyDate`) and a method (`initEntity`) to all entity classes. This ensures consistency and reduces code duplication.
*   **BaseEntityApi and BaseEntitySystemApi:** These interfaces likely extend a common base interface (possibly within `it.water.core`), which could define common methods for entity management, such as `save`, `update`, `remove`, `find`, and `findAll`. They provide a consistent API for interacting with entities and system-level entity operations, respectively.

## Weaknesses and Areas for Improvement

*   [ ] **Provide more context-specific examples for module usage:** The documentation should include more realistic examples demonstrating how to integrate the modules into real-world applications.
*   [ ] **Elaborate more on security aspects and access control mechanisms:** The documentation should provide concrete examples of how to configure and use the security features of the repository, including RBAC and data sharing.
*   [ ] **Incorporate diagrams for better visual understanding:** Add architectural diagrams to illustrate the relationships between modules and components, as well as sequence diagrams to show the flow of requests and events.
*   [ ] **Document the `it.water.core` library:** Provide more information about the `it.water.core` library, including its purpose, components, and how it integrates with the Water Repository project.
*   [ ] **Clarify the role of the `WaterValidator`:** Explain how the `WaterValidator` is used to validate entities and how developers can customize the validation process.
*   [ ] **Provide more information about event handling:** Document the events that are produced by the repository and how other parts of the system can subscribe to these events.
*   [ ] **Add more detailed Javadoc comments:** Improve the Javadoc comments for all classes and methods, providing more context and examples.
*   [ ] **Create a sample application:** Develop a sample application that demonstrates how to use the Water Repository project in a complete, end-to-end scenario.
*   [ ] **Improve error handling and logging:** Add more robust error handling and logging throughout the project.
*   [ ] **Add support for different data storage mechanisms:** Extend the repository to support other data storage mechanisms, such as NoSQL databases.
*   [ ] **Implement caching:** Add caching to improve performance.
*   [ ] **Standardize exception handling:** Ensure consistent exception handling across all modules, providing clear and informative error messages.
*   [ ] **Define clear versioning strategy:** Implement a clear versioning strategy for the project and its modules.
*   [ ] **Improve test coverage:** Increase test coverage to ensure the quality and stability of the project.
*   [ ] **Clarify thread safety:** Document thread safety aspects of the core components.
*   [ ] **Provide a migration guide:** If there are breaking changes between versions, create a migration guide.

## Further Areas of Investigation

*   **Performance Bottlenecks:** Investigate potential performance bottlenecks in the query building and parsing mechanism, as well as in the service layer.
*   **Scalability Considerations:** Analyze the scalability of the repository and identify potential limitations.
*   **Integrations with External Systems:** Explore potential integrations with other systems, such as identity providers, message queues, and monitoring tools.
*   **Advanced Querying Features:** Research and implement advanced querying features, such as full-text search and geospatial queries.
*   **Data Auditing:** Implement data auditing capabilities to track changes to entities over time.
*   **Support for Different Data Formats:** Explore support for different data formats, such as XML and YAML.
*   **Customizable Event Handling:** Allow developers to customize the event handling mechanism, such as by adding custom event listeners or modifying the event payload.
*   **Dynamic Schema Updates:** Investigate the possibility of supporting dynamic schema updates without requiring code changes.
*   **Multi-Tenancy Support:** Explore the possibility of adding multi-tenancy support to the repository.
*   **Data Archiving:** Implement data archiving capabilities to move old or inactive data to a separate storage location.

## Attribution

Generated with the support of ArchAI, an automated documentation system.
