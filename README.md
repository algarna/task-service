# Task Service

A simple **Spring Boot 3** REST API for managing tasks.  
Built with clean architecture principles (`web` / `app` / `domain` / `infra` / `dto`) to mimic enterprise-level projects, even for a small CRUD.

---

## 📦 Features
- CRUD endpoints for tasks (`/api/v1/tasks`)
- DTO-based request/response models
- Validation with Jakarta Bean Validation (`@NotBlank`, `@Size`, …)
- Global error handling with `@RestControllerAdvice`
- Auditing (`createdAt`, `updatedAt`) via Spring Data JPA
- Swagger/OpenAPI 3 documentation (`/swagger-ui.html`)
- Unit tests for the service layer (JUnit 5 + Mockito)

---

## 🛠️ Tech Stack
- **Java 17**
- **Spring Boot 3.3.3**
  - Spring Web
  - Spring Data JPA
  - Validation
- **H2 Database** (in-memory, for dev & tests)
- **springdoc-openapi** (Swagger UI)
- **JUnit 5** + **Mockito** (unit testing)

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven

### Run locally
```bash
./mvnw spring-boot:run
