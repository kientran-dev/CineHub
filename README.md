# CineHub

CineHub is a modern cinema management REST API built with **Spring Boot**, **Spring Security**, **JWT authentication**, and **PostgreSQL** (Spring Data JPA).

## Tech Stack
- **Java**: 17 (configured in `pom.xml`)
- **Spring Boot**: using `spring-boot-starter-parent` (version defined in `pom.xml`)
- **Database**: PostgreSQL + Spring Data JPA
- **Security**: Spring Security + JWT (jjwt)
- **Build tool**: Maven (`mvnw`, `.mvn/`)
- **Validation**: `spring-boot-starter-validation`
- **Monitoring**: Spring Boot Actuator
- **Env loading**: `dotenv-java`

## Project Structure (Layered Architecture)
Source code: `src/main/java/com/kientran/cinehub`

- `config/` – Application configuration (Spring configs)
- `controller/` – REST controllers (API endpoints)
- `service/` – Business logic layer
- `repository/` – Data access layer (JPA repositories)
- `entity/` – Domain models/entities
- `dto/` – Request/Response DTOs
- `security/` – Security configuration & JWT-related components
- `exception/` – Custom exceptions & (potential) global exception handling

Main entry point:
- `CineHubApplication.java`

## API Testing
Repo includes a Postman collection:
- `ImportAPIPostman.json`

## Local Development
### 1) Configure environment variables
There is a `.env` file at repository root. Update it to match your local environment.

### 2) Run the application
```bash
./mvnw spring-boot:run
```

Or on Windows:
```bash
mvnw.cmd spring-boot:run
```

## Notes
- This repository currently targets **PostgreSQL (JPA)** based on the `pom.xml` dependencies.
- Authentication uses **JWT** (JSON Web Token) via `io.jsonwebtoken (jjwt)` libraries.