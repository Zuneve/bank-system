# 🚀 Bank Service

Многомодульное REST-приложение банковской системы. Сервис хранит пользователей, счета и историю операций в PostgreSQL, рассчитывает комиссии переводов и предоставляет API с документацией Swagger.

## ✨ Особенности (Features)

* **Пользователи:** создание, поиск, фильтрация по полу и цвету волос, управление друзьями.
* **Счета:** создание нескольких счетов для одного пользователя и просмотр баланса.
* **Операции:** пополнение, снятие и хранение истории изменений баланса.
* **Переводы:** комиссия `0%` между своими счетами, `3%` при переводе другу и `10%` остальным пользователям.
* **Оптимизация ORM:** связанные сущности загружаются через `@EntityGraph`, чтобы не возникала проблема N+1.
* **Документация API:** Swagger UI с описанием эндпоинтов, кодов ответа и DTO.
* **Тестирование:** бизнес-логика покрыта unit-тестами с Mockito без обращения к реальной БД.

## 🛠️ Стек технологий (Tech Stack)

* **Язык:** Java 17+
* **Сборка:** Maven, многомодульный проект
* **Фреймворк:** Spring Boot 3.5.10, Spring Web
* **База данных:** PostgreSQL 16, Docker Compose
* **Доступ к данным:** Hibernate, Jakarta Persistence, Spring Data JPA
* **Документация:** springdoc-openapi 2.8.17, Swagger UI
* **Маппинг и утилиты:** MapStruct, Lombok
* **Тестирование:** JUnit 5, Mockito

## 📦 Модули

* `domain` — JPA-сущности `User`, `Account`, `Operation` и связанные перечисления.
* `repositories` — интерфейсы репозиториев, Spring Data JPA-адаптеры и настройки PostgreSQL.
* `dto` — модели запросов и ответов REST API с Bean Validation и Swagger-аннотациями.
* `bank-service` — бизнес-логика счетов, пользователей, комиссий и операций.
* `presentation` — REST-контроллеры, MapStruct-мапперы и точка входа приложения.
* `security` — отдельный модуль с базовой конфигурацией Spring Security и ролями; пока не подключён к запускаемому модулю `presentation`.

## 📦 Установка (Installation)

Для запуска потребуются JDK 17+, Maven и Docker.

1. Клонируйте репозиторий:

   ```bash
   git clone <repository-url>
   ```

2. Перейдите в каталог проекта:

   ```bash
   cd bank
   ```

3. Запустите PostgreSQL:

   ```bash
   docker compose up -d
   ```

4. Соберите и установите все модули:

   ```bash
   mvn clean install
   ```

PostgreSQL доступен на `localhost:5433`. Используются база `bank_db`, пользователь `bank_user` и пароль `bank_password`.

## 🚀 Запуск (Usage)

Запустите REST-приложение:

```bash
mvn -f presentation/pom.xml spring-boot:run
```

После появления строки `Started Main` доступны:

* REST API — `http://localhost:8080/api`
* Swagger UI — `http://localhost:8080/swagger-ui/index.html`
* OpenAPI JSON — `http://localhost:8080/v3/api-docs`

Основные группы эндпоинтов:

* `/api/users` — пользователи и друзья;
* `/api/accounts` — счета, баланс, пополнение, снятие и переводы;
* `/api/operations` — история операций с фильтрацией.

Остановить PostgreSQL:

```bash
docker compose down
```

Удалить контейнер вместе с тестовыми данными:

```bash
docker compose down -v
```

## ✅ Проверка

Запуск всех тестов:

```bash
mvn test
```

SQL-запросы Hibernate выводятся в консоль благодаря `spring.jpa.show-sql=true`. Для проверки отсутствия N+1 вызовите:

```bash
curl http://localhost:8080/api/accounts
```

Список счетов и их владельцы должны загружаться одним SQL-запросом с `JOIN`, без отдельных запросов для каждого владельца.

## 👤 Автор 
#### Антон Гусев 
#### tg: zuneve
#### mail: antonyogusev@gmail.com
