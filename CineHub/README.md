# CineHub — Backend API

Spring Boot REST API for **CineHub**, a movie streaming platform.

---

## Tech Stack

- **Java 17** + **Spring Boot**
- **Spring Security** + **JWT** (access token: 15m, refresh token: 7d)
- **Google OAuth2.0** authentication
- **PostgreSQL** database (JPA/Hibernate)
- **VNPay** payment gateway integration
- **Gmail SMTP** for email (OTP, password reset)
- **Maven** build tool

---

## Prerequisites

- Java 17+
- Maven (or use `./mvnw`)
- PostgreSQL running locally
- A `.env` file configured (see below)

---

## Environment Variables

Create a `.env` file in this folder (`CineHub/`) with the following:

```env
DB_PASSWORD=your_postgres_password
JWT_SECRET=your_jwt_secret_key
VNP_HASH_SECRET=your_vnpay_hash_secret
MAIL_USERNAME=your_gmail_address
MAIL_PASSWORD=your_gmail_app_password
GOOGLE_CLIENT_ID=your_google_oauth_client_id
```

---

## Getting Started

```bash
# Clone the repo (if not already)
cd CineHub

# Run with Maven wrapper
./mvnw spring-boot:run
```

The API will be available at: `http://localhost:8080`

---

## API Collection

Import `ImportAPIPostman.json` into Postman to test all available endpoints.

---

## Database

- Engine: **PostgreSQL**
- Default database name: `cinehub`
- Default host: `localhost:5432`
- Schema is auto-managed by Hibernate (`ddl-auto=update`)

---

## License

[MIT](LICENSE)
