# S4.02 — Spring Boot REST API with MySQL

## Project Goal & Overview

The goal of this project is to build a **REST API** using Spring Boot to manage the stock of a fruit shop, now incorporating **providers** (suppliers). Each fruit is associated with a provider, allowing the system to track the origin of each product.

This project uses **MySQL** as the database and introduces entity relationships with JPA (`@ManyToOne`).

This project covers:

- Creating a REST API with Spring Boot following the MVC pattern
- Persisting data with Spring Data JPA and MySQL
- Managing entity relationships with `@ManyToOne`
- Using DTOs with Bean Validation (`@NotBlank`, `@Positive`, `@NotNull`)
- Centralized exception handling with `GlobalExceptionHandler`
- Unit testing with Mockito
- Multi-stage Dockerfile for production builds
- Docker Compose for local MySQL setup
- Database connection configured via environment variables

---

## 📌 Models

### Provider

| Field     | Type     | Description                       |
|-----------|----------|-----------------------------------|
| `id`      | `Long`   | Unique identifier, auto-generated |
| `name`    | `String` | Provider name (unique)            |
| `country` | `String` | Provider country                  |

### Fruit

| Field          | Type       | Description                        |
|----------------|------------|------------------------------------|
| `id`           | `Long`     | Unique identifier, auto-generated  |
| `name`         | `String`   | Name of the fruit                  |
| `weightInKilos`| `int`      | Weight in kilograms                |
| `provider`     | `Provider` | Associated provider (`@ManyToOne`) |

---

## 📌 Endpoints

### Providers

| Method   | URL               | Description            | Success Code |
|----------|-------------------|------------------------|--------------|
| `POST`   | `/providers`      | Create a new provider  | `201`        |
| `GET`    | `/providers`      | Get all providers      | `200`        |
| `GET`    | `/providers/{id}` | Get a provider by id   | `200`        |
| `PUT`    | `/providers/{id}` | Update a provider      | `200`        |
| `DELETE` | `/providers/{id}` | Delete a provider      | `204`        |

### Fruits

| Method   | URL                          | Description                    | Success Code |
|----------|------------------------------|--------------------------------|--------------|
| `POST`   | `/fruits`                    | Create a new fruit             | `201`        |
| `GET`    | `/fruits`                    | Get all fruits                 | `200`        |
| `GET`    | `/fruits?providerId={id}`    | Get fruits by provider         | `200`        |
| `GET`    | `/fruits/{id}`               | Get a fruit by id              | `200`        |
| `PUT`    | `/fruits/{id}`               | Update a fruit                 | `200`        |
| `DELETE` | `/fruits/{id}`               | Delete a fruit                 | `204`        |

### POST /providers — Example Request

```json
{
  "name": "FruitCo",
  "country": "Spain"
}
```

### POST /providers — Example Response (201 Created)

```json
{
  "id": 1,
  "name": "FruitCo",
  "country": "Spain"
}
```

### POST /fruits — Example Request

```json
{
  "name": "Apple",
  "weightInKilos": 5,
  "providerId": 1
}
```

### POST /fruits — Example Response (201 Created)

```json
{
  "id": 1,
  "name": "Apple",
  "weightInKilos": 5,
  "providerId": 1,
  "providerName": "FruitCo"
}
```

### Error Cases

**Provider not found (404):**
```json
{
  "error": "Provider not found with id: 99"
}
```

**Duplicate provider name (400):**
```json
{
  "error": "Provider with name 'FruitCo' already exists"
}
```

**Cannot delete provider with fruits (400):**
```json
{
  "error": "Cannot delete provider with id: 1 because it has associated fruits"
}
```

---

## 📌 Class Structure

| Class                      | Package        | Role                                              |
|----------------------------|----------------|----------------------------------------------------|
| `Fruit`                    | `model`        | JPA entity with `@ManyToOne` to Provider           |
| `Provider`                 | `model`        | JPA entity with unique name constraint             |
| `FruitDTO`                 | `dto`          | DTO with validation and providerId/providerName    |
| `ProviderDTO`              | `dto`          | DTO with validation                                |
| `FruitRepository`          | `repository`   | JPA repository with custom query methods           |
| `ProviderRepository`       | `repository`   | JPA repository with existsByName                   |
| `FruitService`             | `services`     | Interface defining fruit operations                |
| `FruitServiceImpl`         | `services`     | Service implementation with provider validation    |
| `ProviderService`          | `services`     | Interface defining provider operations             |
| `ProviderServiceImpl`      | `services`     | Service implementation with duplicate check        |
| `FruitController`          | `controllers`  | REST controller with CRUD + filter by provider     |
| `ProviderController`       | `controllers`  | REST controller with CRUD endpoints                |
| `FruitNotFoundException`   | `exception`    | Custom exception returning 404                     |
| `ProviderNotFoundException`| `exception`    | Custom exception returning 404                     |
| `ProviderDuplicateException`| `exception`   | Custom exception returning 400                     |
| `GlobalExceptionHandler`   | `exception`    | Centralized error handling for all controllers     |

---

## 🧪 Tests

### Unit Tests — `ProviderServiceTest`

| Test                                          | Description                                    |
|-----------------------------------------------|------------------------------------------------|
| `createProvider_shouldReturnCreatedProvider`   | Verifies provider creation                     |
| `createProvider_shouldThrowWhenDuplicateName`  | Verifies 400 for duplicate name                |
| `getAllProviders_shouldReturnList`             | Verifies all providers are returned            |
| `getProviderById_shouldReturnProvider`         | Verifies retrieval by id                       |
| `getProviderById_shouldThrowWhenNotFound`      | Verifies 404 for missing id                    |
| `updateProvider_shouldReturnUpdatedProvider`   | Verifies provider update                       |
| `deleteProvider_shouldDeleteWhenNoFruits`       | Verifies deletion when no fruits associated    |
| `deleteProvider_shouldThrowWhenHasFruits`       | Verifies 400 when provider has fruits          |
| `deleteProvider_shouldThrowWhenNotFound`        | Verifies 404 for missing id                    |

### Unit Tests — `FruitServiceTest`

| Test                                              | Description                                |
|---------------------------------------------------|--------------------------------------------|
| `createFruit_shouldReturnCreatedFruit`             | Verifies fruit creation with provider      |
| `createFruit_shouldThrowWhenProviderNotFound`      | Verifies 404 when provider missing         |
| `getFruitById_shouldReturnFruit`                   | Verifies retrieval by id                   |
| `getFruitById_shouldThrowWhenNotFound`             | Verifies 404 for missing id                |
| `getAllFruits_shouldReturnList`                     | Verifies all fruits are returned           |
| `getFruitsByProviderId_shouldReturnList`            | Verifies filtering by provider id          |
| `getFruitsByProviderId_shouldThrowWhenProviderNotFound` | Verifies 404 when provider missing    |
| `updateFruit_shouldReturnUpdatedFruit`             | Verifies fruit update                      |
| `deleteFruit_shouldDeleteWhenExists`               | Verifies deletion of existing fruit        |
| `deleteFruit_shouldThrowWhenNotFound`              | Verifies 404 for missing id                |

---

## 🐳 Docker

### Docker Compose (MySQL)

Start the MySQL database:

```bash
docker compose up -d
```

### Dockerfile

The project includes a multi-stage `Dockerfile`:

- **Stage 1 (Build):** Compiles the application using Maven and generates the `.jar` file.
- **Stage 2 (Run):** Copies only the `.jar` into a lightweight JRE Alpine image.

### Build and run:

```bash
docker build -t fruit-api-mysql .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/fruitdb \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=root \
  fruit-api-mysql
```

---

## 🚀 Getting Started

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   ```

2. **Navigate to the project folder:**
   ```bash
   cd fruit-api-mysql
   ```

3. **Start MySQL with Docker Compose:**
   ```bash
   docker compose up -d
   ```

4. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   The server will start on port **8080**.

5. **Run the tests:**
   ```bash
   ./mvnw test
   ```

---

## 🛠️ Tech Stack

- Java 21
- Spring Boot 4.0.5
- Spring Data JPA
- MySQL 8.0
- Bean Validation
- Lombok
- JUnit 5
- Mockito
- Docker & Docker Compose
