# Repository Module — CRUD Abstraction Layer

## Purpose
Provides the technology-agnostic persistence abstractions and base service implementations for all Water entities. Defines the entity hierarchy, query/filter model, pagination, and the two-layer service pattern (Api + SystemApi). Does NOT contain JPA-specific code — that lives in `JpaRepository`.

## Sub-modules

| Sub-module | Package | Role |
|---|---|---|
| `Repository-entity` | `it.water.repository.entity` | Base entity classes, `PaginatedResult`, domain exceptions |
| `Repository-persistence` | `it.water.repository.persistence` | `DefaultQueryBuilder`, `QueryParser`, filter/sort/pagination support |
| `Repository-service` | `it.water.repository.service` | `BaseEntityServiceImpl`, `BaseEntitySystemServiceImpl`, `OwnedChildBaseEntityServiceImpl` |

## Entity Hierarchy

```
BaseEntity (Core-api interface)
  └─ AbstractEntity (Repository-entity)
       └─ AbstractJpaEntity (JpaRepository-api — with @Id, @Version)
            └─ AbstractJpaExpandableEntity (for dynamic field extension)
```

### AbstractEntity Fields
- `id` (long), `entityVersion` (int — optimistic locking), `entityCreateDate`, `entityModifyDate`

### Entity Markers (interfaces)
- **`ProtectedEntity`** — Participates in the permission system (`@AccessControl`)
- **`OwnedResource`** — Has `ownerUserId`; `findAll` is filtered to logged-in user's entities
- **`SharedEntity`** — Can be shared to other users via `WaterSharedEntity`
- **`AbstractJpaExpandableEntity`** — Supports dynamic extra fields stored as JSON

## PaginatedResult<T>

```java
PaginatedResult<MyEntity> result = myApi.findAll(delta, page, query);
result.getResults();    // List<T> current page
result.getNumPages();   // total pages
result.getCurrentPage();
result.getDelta();      // page size
result.hasNextPage();
```

## Query / Filter Model (Repository-persistence)

```java
// Programmatic query
Query query = new Query();
query.addFilter(new QueryFilter("name", QueryType.EQUALS, "John"));
query.addOrder(new QueryOrder("entityCreateDate", QueryOrderType.DESC));

// String-based filter parsing (used in REST layer)
// "name=John AND age>25 OR status=ACTIVE"
Query parsed = QueryParser.parse("name=John AND status=ACTIVE");
```

### Supported QueryTypes
`EQUALS, NOT_EQUALS, GREATER_THAN, LESS_THAN, GREATER_OR_EQUAL, LESS_OR_EQUAL, LIKE, IN, NOT_IN, IS_NULL, IS_NOT_NULL`

## Service Layer Pattern (Repository-service)

### BaseEntityServiceImpl — Api (with permission checks)
```java
// Extends this for every entity's public service
public abstract class BaseEntityServiceImpl<T extends BaseEntity>
    implements BaseEntityApi<T> {
    // All CRUD methods run through @AllowPermissions interceptors
}
```

### BaseEntitySystemServiceImpl — SystemApi (no permission checks)
```java
// Extends this for every entity's system/internal service
public abstract class BaseEntitySystemServiceImpl<T extends BaseEntity>
    implements BaseEntitySystemApi<T> {
    // Bypasses interceptors — for trusted internal operations only
}
```

### OwnedChildBaseEntityServiceImpl
For entities that are children of an `OwnedResource` — automatically filters by parent ownership.

## Domain Exceptions

| Exception | HTTP Status | When |
|---|---|---|
| `EntityNotFound` | 404 | Entity with given ID doesn't exist |
| `DuplicateEntityException` | 409 | Unique constraint violation |
| `NoResultException` | 404 | Query returned no results |
| `ValidationException` | 422 | Bean validation failure |
| `UnauthorizedException` | 401 | Permission check failed |
| `WaterRuntimeException` | 500 | Generic framework error (includes optimistic lock conflict) |

## Dependencies
- `it.water.core:Core-api` — base interfaces
- `it.water.core:Core-model` — base exceptions
- `it.water.core:Core-security` — permission annotations
- `com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider` — JSON views on entities

## Testing
Use `WaterTestExtension` with the full persistence stack (JpaRepository + H2 in-memory):

```java
@ExtendWith(WaterTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyEntityApiTest implements Service {
    @Inject @Setter private MyEntityApi api;
    @Inject @Setter private MyEntitySystemApi systemApi;
    @Inject @Setter private MyEntityRepository repository;
    // Test CRUD + validation + permissions
}
```

## Code Generation Rules
- Every entity MUST extend `AbstractEntity` (or `AbstractJpaEntity` for JPA persistence)
- Every entity with access control MUST implement `ProtectedEntity` and annotate with `@AccessControl`
- Every entity owned by a user MUST implement `OwnedResource` and provide `ownerUserId`
- `SystemApi` tests bypass permission setup — use for verifying underlying data operations
- NEVER put JPA annotations (`@Entity`, `@Column`, `@Table`) in this module — use `JpaRepository-api` sub-classes
