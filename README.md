# Bank Service

Bank Service is a multi-module Spring Boot application for a simple banking system. It supports users, accounts, friends, operation history, transfers with commissions, role-based access, and balance conversion using exchange rates delivered through Kafka.

## What It Can Do

* Create administrators and clients.
* Store users, accounts, authentication accounts, and operations in PostgreSQL.
* Create accounts for users.
* Deposit, withdraw, and transfer money.
* Calculate transfer commissions:
  * `0%` between accounts of the same user;
  * `3%` when transferring to a friend;
  * `10%` for other users.
* Protect endpoints with Spring Security roles:
  * `ADMIN`;
  * `CLIENT`.
* Generate changing USD and EUR exchange rates in a separate service.
* Publish rate updates and process on-demand rate requests through Kafka.
* Return account balances and operation amounts converted from RUB.
* Cache recent exchange rates in the banking application.
* Show API documentation in Swagger UI.

## Tech Stack

* Java 22
* Spring Boot 3
* Spring Web
* Spring Security
* Spring Data JPA
* Spring for Apache Kafka
* Hibernate / JPA
* PostgreSQL
* Apache Kafka
* Maven
* Docker Compose
* Lombok
* MapStruct
* JUnit 5 / Mockito
* springdoc-openapi

## Project Modules

| Module | Purpose |
| --- | --- |
| `domain` | JPA entities and enums. |
| `repositories` | Spring Data repositories, adapters, specifications, database config. |
| `dto` | Request/response DTOs and MapStruct mappers. |
| `bank-service` | Business logic for users, accounts, transfers, and operations. |
| `security` | Spring Security config, auth users, roles, ownership checks. |
| `presentation` | REST controllers and application entry point. |
| `rates-service` | Scheduled USD/EUR rate generation and Kafka request/reply handling. |

## Exchange Rates

All account balances and operation amounts are stored in RUB. The separate `rates-service` generates USD and EUR rates every 10 seconds and publishes them to the `rates.updates` Kafka topic.

The banking application keeps recent rates in an in-memory cache. If a requested rate is absent or older than 30 seconds, it sends a request through `rates.requests` and waits for a response on `rates.replies` for up to 3 seconds.

Kafka topics:

| Topic | Purpose |
| --- | --- |
| `rates.updates` | Periodic USD/EUR rate updates. |
| `rates.requests` | On-demand requests from the banking application. |
| `rates.replies` | Replies from `rates-service`. |

## Security

The current implementation uses HTTP Basic authentication.

Default development admin:

```text
username: admin
password: admin
```

Access rules:

* unauthenticated users receive `401`;
* admins can create users/admins/clients and read all users/accounts;
* clients can read and modify only their own profile, friends, and accounts;
* access to another role's endpoints returns `403`.

CSRF is currently disabled because the API is tested with HTTP Basic. If the project switches to cookie/session login, CSRF should be enabled again.

## Run Locally

Start PostgreSQL, Kafka, and Kafka UI:

```bash
docker compose up -d
```

Build the project:

```bash
mvn clean install
```

Run the rates service:

```bash
mvn -f rates-service/pom.xml spring-boot:run
```

In another terminal, run the banking application:

```bash
mvn -f presentation/pom.xml spring-boot:run
```

Open:

* API: `http://localhost:8080/api`
* Swagger UI: `http://localhost:8080/swagger-ui/index.html`
* OpenAPI JSON: `http://localhost:8080/v3/api-docs`
* Kafka UI: `http://localhost:8085`

Database settings:

```text
url: jdbc:postgresql://localhost:5433/bank_db
username: bank_user
password: bank_password
```

## Main Endpoints

### Auth

| Method | Path | Role | Description |
| --- | --- | --- | --- |
| `POST` | `/api/auth/admins` | `ADMIN` | Create an admin account. |
| `POST` | `/api/auth/clients` | `ADMIN` | Create a banking user and linked client account. |

### Admin

| Method | Path | Role | Description |
| --- | --- | --- | --- |
| `GET` | `/api/admin/users` | `ADMIN` | Get users with optional filters. |
| `GET` | `/api/admin/users/{userId}` | `ADMIN` | Get a user by id. |
| `GET` | `/api/admin/users/{userId}/accounts` | `ADMIN` | Get user's accounts. |
| `GET` | `/api/admin/accounts` | `ADMIN` | Get all accounts. |
| `GET` | `/api/admin/accounts/{accountId}` | `ADMIN` | Get account with operations. |
| `GET` | `/api/admin/accounts/{accountId}/operations` | `ADMIN` | Get account operations. |

### Client

| Method | Path | Role | Description |
| --- | --- | --- | --- |
| `GET` | `/api/client/me` | `CLIENT` | Get current client profile. |
| `GET` | `/api/client/me/friends` | `CLIENT` | Get current client's friends. |
| `POST` | `/api/client/me/friends/{friendId}` | `CLIENT` | Add a friend. |
| `DELETE` | `/api/client/me/friends/{friendId}` | `CLIENT` | Remove a friend. |
| `GET` | `/api/client/accounts` | `CLIENT` | Get current client's accounts. |
| `GET` | `/api/client/accounts/{accountId}` | `CLIENT` | Get own account by id. |
| `GET` | `/api/client/accounts/{accountId}/balance?currency=USD` | `CLIENT` | Get own balance converted from RUB. |
| `POST` | `/api/client/accounts/{accountId}/deposit` | `CLIENT` | Deposit money to own account. |
| `POST` | `/api/client/accounts/{accountId}/withdraw` | `CLIENT` | Withdraw money from own account. |
| `POST` | `/api/client/accounts/transfer` | `CLIENT` | Transfer money from own account. |

Legacy endpoints under `/api/users`, `/api/accounts`, and `/api/operations` are kept for previous labs and are protected with `ADMIN`. The account balance and operation endpoints accept an optional `currency=USD|EUR|RUB` query parameter.

## Quick Check

Anonymous request should fail:

```bash
curl -i http://localhost:8080/api/admin/users
```

Admin request should work:

```bash
curl -i -u admin:admin http://localhost:8080/api/admin/users
```

Create a client:

```bash
curl -i -u admin:admin \
  -X POST http://localhost:8080/api/auth/clients \
  -H "Content-Type: application/json" \
  -d '{
    "username": "client1",
    "password": "clientPassword123",
    "login": "client1_login",
    "name": "Client One",
    "age": 20,
    "male": true,
    "hairColor": "Black"
  }'
```

Client request should work:

```bash
curl -i -u client1:clientPassword123 http://localhost:8080/api/client/me
```

Client request to admin API should fail:

```bash
curl -i -u client1:clientPassword123 http://localhost:8080/api/accounts
```

Expected status: `403`.

## Tests

```bash
mvn test
```

## Author

Anton Gusev

* Telegram: `zuneve`
* Email: `antonyogusev@gmail.com`
