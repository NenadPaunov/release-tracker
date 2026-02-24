# Release Tracker API

Simple, production-ready Spring Boot application for tracking software releases. Built as a technical challenge with a focus on auditability, security, and automated code quality.

## Tech Stack
* **Java 21** & **Spring Boot 3.4**
* **PostgreSQL** & **Flyway** (Migrations)
* **Keycloak** (OAuth2 / RBAC)
* **Hibernate Envers** (Audit History)
* **Testcontainers** (Integration Tests)
* **MapStruct** & **Lombok**
* **Google Java Format** (Strict code style)

## Key Features
* **Full Audit Trail:** Every change is tracked. Beyond `createdAt`/`lastUpdateAt`, there is a dedicated history endpoint powered by `Hibernate Envers` to see every version of a record.
* **Security:** Role-based access control (RBAC). Admins have full RW access, while regular users have limited read-only permissions.
* **Soft Delete:** Deleting a release doesn't remove it from the DB; it sets a `deleted` flag and a timestamp, keeping the data available for audit.
* **Optimistic Locking:** Prevention of "lost updates" using JPA versioning.
* **Code Quality:** Strict quality gates with `JaCoCo` (80%+ coverage) and automated code formatting.
* **Observability:** Correlation IDs in every response and structured logging via `Logstash encoder`.

---

## API Endpoints
All endpoints are versioned under `/v1/releases`.

| Method | Endpoint | Description | Required Role |
| :--- | :--- | :--- | :--- |
| **GET** | `/v1/releases` | List with filters & pagination | `USER` |
| **GET** | `/v1/releases/{uuid}` | Single release details | `USER` |
| **POST** | `/v1/releases` | Create new release | `ADMIN` |
| **PUT** | `/v1/releases/{uuid}` | Update (requires `version` in body) | `ADMIN` |
| **DELETE**| `/v1/releases/{uuid}` | Soft delete | `ADMIN` |
| **GET** | `/v1/releases/{uuid}/history` | Audit trail (Full history) | `ADMIN` |

---

## Getting Started

### Prerequisites
* Docker & Docker Compose
* Maven 3.9+

### Run Everything (Docker)
This is the easiest way to start. It spins up the App, Postgres, and Keycloak (with a pre-configured realm and users).
```bash
docker-compose up --build
```

### Local Development
If you want to run the application from your IDE:

### Start infrastructure:

```bash
docker-compose up -d postgres keycloak
```
Run app with local profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```
Note: The build will fail if code is not properly formatted. To fix formatting, run:

```bash
mvn git-code-format:format-code
```

### Technical Implementation Details
Security & Authentication
Keycloak is used as the Identity Provider. To keep things lightweight, the UI is disabled and the realm is initialized via `keycloak-realm.json`.

Admin User: `admin` / `admin`

Regular User: `user` / `user`

### Testing
The project maintains high testing standards:

Unit Tests: Focus on business logic in services.

Integration Tests: Use Testcontainers to ensure the code works against a real PostgreSQL database.

``` bash
mvn clean verify
```

### Documentation & Tools
Swagger UI: http://localhost:8080/release-tracker/api/swagger-ui/index.html

### Postman: 
Collections are available in the `/postman` directory.

### Database Migrations: 
Managed by `Flyway` (scripts in src/main/resources/db/migration).
