# Hostelgrid Backend

A Spring Boot microservices backend for hostel management system with JWT authentication and modular architecture.

## Architecture

- **Multi-module Maven project** with shared common components
- **Microservices architecture** for scalability
- **JWT-based authentication** for secure API access
- **Spring Security** integration for authorization

## Modules

### auth-service
Authentication microservice handling user login, registration, and JWT token management.

**Features:**
- User authentication with phone/email
- JWT token generation and validation
- Role-based access control (STUDENT, ADMIN, OWNER)
- Secure password handling

### common-module
Shared utilities and components across all microservices.

**Components:**
- JWT token provider and validation
- Common security configurations
- Shared DTOs and utilities

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.0-SNAPSHOT**
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database operations
- **JWT** - Token-based authentication
- **Lombok** - Code generation
- **Maven** - Dependency management

## Database Schema

User entity with:
- Unique phone number and email constraints
- Role-based permissions
- Audit timestamps
- Account status management

## Getting Started

1. Build the project: `mvn clean install`
2. Run auth-service: `mvn spring-boot:run -pl auth-service`
3. Configure database connection in `application.yml`

## Project Structure

```
Hostelgrid-Backend-Springboot/
├── auth-service/                    # Authentication microservice
│   ├── src/main/java/com/hostelgrid/authservice/
│   │   ├── config/                  # Security & JWT configuration
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtConfigurer.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/              # REST controllers
│   │   │   └── AuthController.java
│   │   ├── dto/                     # Data transfer objects
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   └── SignUpRequest.java
│   │   ├── model/                   # JPA entities
│   │   │   ├── Role.java
│   │   │   └── User.java
│   │   ├── repository/              # Data access layer
│   │   │   └── UserRepository.java
│   │   ├── security/                # Custom security components
│   │   │   ├── CustomUserDetails.java
│   │   │   └── CustomUserDetailsService.java
│   │   └── AuthServiceApplication.java
│   ├── src/main/resources/
│   │   └── application.yml
│   └── pom.xml
|
├── common-module/                   # Shared components
│   ├── src/main/java/com/hostelgrid/common/
│   │   ├── exception/               # Global exception handling
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── response/                # Common response DTOs
│   │   │   └── MessageResponse.java
│   │   └── security/                # JWT utilities
│   │       └── JwtTokenProvider.java
│   └── pom.xml
|
├── pom.xml                          # Parent POM
└── README.md
```

## API Endpoints

- `POST /auth/login` - User authentication
- `POST /auth/register` - User registration
- JWT token required for protected endpoints