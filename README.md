# CineHub 🎬

A modern Spring Boot 3 REST API for cinema management with user registration and movie data handling using MongoDB.

## 🚀 Features

- **User Management**: Registration with email validation and password encryption
- **Movie Database**: Complete movie entity with genres, ratings, and metadata
- **RESTful API**: Clean REST endpoints for seamless integration
- **MongoDB Integration**: Document-based storage with auditing support
- **Security**: Spring Security with BCrypt password hashing
- **Validation**: Input validation with custom error handling

## 🛠️ Tech Stack

- **Java 21** - Latest LTS version
- **Spring Boot 3.5.3** - Framework for rapid development
- **Spring Data MongoDB** - NoSQL database integration
- **Spring Security** - Authentication and authorization
- **Spring Validation** - Input validation
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

## 📋 Prerequisites

- Java 21+
- MongoDB running on `localhost:27017`
- Maven (or use included wrapper)

## 🏃‍♂️ Quick Start

1. **Clone the repository**
   ```powershell
   git clone <your-repo-url>
   cd CineHub
   ```

2. **Start MongoDB** (if not running)
   ```powershell
   # Make sure MongoDB is running on localhost:27017
   ```

3. **Build and run**
   ```powershell
   ./mvnw.cmd clean package
   ./mvnw.cmd spring-boot:run
   ```

4. **Access the application**
   - Server runs on: `http://localhost:8080`
   - Database: `cinehub_db` on MongoDB

## 🔧 Configuration

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/cinehub_db
```

## 📁 Project Structure

```
src/main/java/com/kientran/cinehub/
├── config/          # Configuration
├── controller/      # REST endpoints  
├── dto/            # Request/Response objects
├── entity/         # Database models
├── enums/          # Enumerations
├── exception/      # Custom exceptions
├── repository/     # Data access
└── service/        # Business logic
```

## 🧪 Testing

Run tests with Maven wrapper:

```powershell
./mvnw.cmd test
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

Made with ❤️ by [Kien Tran]