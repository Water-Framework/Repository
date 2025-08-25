# Repository Module

## Module Goal
Il modulo Repository fornisce le astrazioni e le implementazioni di base per la gestione della persistenza, delle entità e delle operazioni CRUD all'interno del Water Framework. Offre un modello unificato per la gestione di entità, query, eccezioni, servizi e validazione, fungendo da fondamento per tutti i moduli che necessitano di persistenza e gestione dati.

## Module Technical Characteristics

### Tecnologie principali
- **JPA (Jakarta Persistence API):** Supporto alla persistenza ORM tramite entità astratte e servizi.
- **Water Core Modules:** Integrazione con i moduli core, validation, registry, permission e service del framework.
- **Spring/OSGi Ready:** Componente utilizzabile sia in ambienti Spring che OSGi.
- **Gestione Query:** Costruttori di query, parser e supporto a filtri, ordinamenti e paginazione.
- **Lombok:** Riduzione del boilerplate.
- **JUnit 5/Mockito:** Test di integrazione e validazione dei servizi e delle policy.

### Componenti architetturali
- **Entity Layer (`Repository-entity`)**
  - `AbstractEntity`, `AbstractJpaEntity`: Base classi per entità persistenti, con supporto a auditing e metadati.
  - `PaginatedResult`: Supporto a risultati paginati.
  - Eccezioni custom: `DuplicateEntityException`, `EntityNotFound`, `NoResultException`.
- **Persistence Layer (`Repository-persistence`)**
  - `DefaultQueryBuilder`, `QueryParser`: Costruzione e parsing di query dinamiche.
- **Service Layer (`Repository-service`)**
  - `BaseEntityServiceImpl`, `BaseEntitySystemServiceImpl`: Implementazioni astratte di servizi CRUD, con supporto a permission, ownership, validazione e integrazione con il runtime.
  - `OwnedChildBaseEntityServiceImpl`: Supporto a entità figlie e relazioni di ownership.
- **Test Layer**
  - Test di integrazione su CRUD, validazione, ownership, permessi, eccezioni e policy.

### Caratteristiche chiave
- CRUD generico e riutilizzabile per tutte le entità
- Supporto a ownership, shared entity, validazione e permission
- Gestione avanzata di query, filtri, ordinamenti e paginazione
- Gestione centralizzata delle eccezioni di persistenza
- Servizi estendibili e personalizzabili

## Permission and Security
- **Controllo permessi:**
  - Annotazioni `@AllowPermissions`, `@AllowGenericPermissions`, `@AllowPermissionsOnReturn` su tutti i metodi CRUD
  - Supporto automatico a ownership e shared entity: filtri e controlli su risorse di proprietà dell'utente loggato
  - Gestione eccezioni di sicurezza (`UnauthorizedException`)
- **Validazione:**
  - Validazione automatica su entità tramite il core validation system
  - Gestione ottimistica delle versioni per update concorrenti
- **Policy di accesso:**
  - Policy configurabili tramite annotazioni e ruoli

## How to Use It

### 1. Import del modulo
Aggiungi il modulo Repository e i suoi sottoprogetti al tuo progetto:

```gradle
implementation 'it.water.repository:Repository-entity:${waterVersion}'
implementation 'it.water.repository:Repository-persistence:${waterVersion}'
implementation 'it.water.repository:Repository-service:${waterVersion}'
```

### 2. Definisci la tua entità
```java
@Entity
public class MyEntity extends AbstractJpaEntity {
    // campi e metodi custom
}
```

### 3. Estendi il servizio base
```java
@FrameworkComponent
public class MyEntityService extends BaseEntityServiceImpl<MyEntity> {
    public MyEntityService() {
        super(MyEntity.class);
    }
    @Override
    protected BaseEntitySystemApi<MyEntity> getSystemService() {
        // restituisci la tua implementazione di sistema
    }
}
```

### 4. Utilizzo CRUD e query
```java
// Salva una nuova entità
myEntityService.save(entity);
// Trova per ID
MyEntity found = myEntityService.find(1L);
// Trova con filtro
Query filter = ...;
MyEntity found = myEntityService.find(filter);
// Trova tutti con paginazione
PaginableResult<MyEntity> results = myEntityService.findAll(filter, 10, 1, null);
// Rimuovi
myEntityService.remove(1L);
```

## Properties and Configurations

### Proprietà principali
- **Nessuna proprietà obbligatoria specifica:** il modulo si integra con le property core del framework e con la configurazione JPA standard.
- **Gestione persistence unit:** configurabile tramite JPA/Spring/OSGi.
- **Gestione ownership:** automatico per entità che implementano `OwnedResource`.
- **Gestione paginazione:** tramite `PaginatedResult` e parametri `delta`, `page`.

### Proprietà dai test
- I test utilizzano utenti, ruoli e permessi di default
- Persistence unit di default: `water-default-persistence-unit`
- Mock e override di repository e servizi per testare policy, ownership, validazione ed eccezioni

## How to Customize Behaviours for This Module

### 1. Estendere servizi e repository
Estendi `BaseEntityServiceImpl` o `BaseEntitySystemServiceImpl` per aggiungere logica custom:
```java
@FrameworkComponent
public class CustomEntityService extends BaseEntityServiceImpl<MyEntity> {
    // logica custom
}
```

### 2. Personalizzare validazione e ownership
Override dei metodi di validazione o dei filtri di ownership per logiche avanzate.

### 3. Gestione eccezioni custom
Aggiungi nuove eccezioni o gestisci casi particolari estendendo le eccezioni di base.

### 4. Test personalizzati
Utilizza i test di esempio (`WaterRepositoryServiceTest`) come base per testare policy, ownership, validazione e permessi custom.

---

Il modulo Repository fornisce la base solida, estendibile e cross-platform per la gestione della persistenza, delle entità e dei servizi CRUD nelle applicazioni Water Framework, con supporto avanzato a ownership, validazione, permission e testabilità.

