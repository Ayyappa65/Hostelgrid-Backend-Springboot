# Hostelgrid Backend

A Spring Boot microservices backend for hostel management system with JWT authentication and modular architecture.

## Architecture

- **Multi-module Maven project** with shared common components
- **Microservices architecture** for scalability
- **JWT-based authentication** for secure API access
- **Spring Security** integration for authorization

## Modules

### service-registry
Eureka service registry for microservice discovery and registration.

**Features:**
- Service discovery and registration
- Health monitoring
- Load balancing support
- Secured dashboard (admin:Admin@123)

### api-gateway
Spring Cloud Gateway for routing and load balancing.

**Features:**
- Dynamic service routing
- Load balancing
- Request/response filtering
- Centralized entry point

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
- **Spring Boot 3.4.1**
- **Spring Cloud 2024.0.0** - Microservices framework
- **Spring Cloud Gateway** - API Gateway
- **Netflix Eureka** - Service discovery
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database operations
- **Spring Boot Actuator** - Health monitoring & metrics
- **JWT** - Token-based authentication
- **MySQL** - Database
- **Lombok** - Code generation
- **Maven** - Dependency management

## Database Schema

User entity with:
- Unique phone number and email constraints
- Role-based permissions
- Audit timestamps
- Account status management

## Getting Started

1. **Prerequisites:**
   - Java 21
   - MySQL database
   - Maven 3.6+

2. **Setup Database:**
   - Create MySQL database: `hostelgrid_auth_db`
   - Update credentials in `auth-service/src/main/resources/application.yml`

3. **Build and Run:**
   ```bash
   mvn clean install
   
   # Start services in order:
   mvn spring-boot:run -pl service-registry    # Port 8761
   mvn spring-boot:run -pl api-gateway         # Port 8080
   mvn spring-boot:run -pl auth-service        # Port 8081
   ```

4. **Access Points:**
   - API Gateway: http://localhost:8080
   - Eureka Dashboard: http://localhost:8761 (admin:Admin@123)
   - Auth Service: http://localhost:8081

## Project Structure

```
Hostelgrid-Backend-Springboot/
├── service-registry/                # Eureka service registry
│   ├── src/main/java/com/hostelgrid/serviceregistry/
│   │   ├── config/SecurityConfig.java      # Dashboard security config (protects Eureka dashboard with authentication while allowing service registration)
│   │   └── ServiceRegistryApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
|
├── api-gateway/                     # Spring Cloud Gateway
│   ├── src/main/java/com/hostelgrid/apigateway/
│   │   ├── config/SecurityConfig.java       # Gateway security config (disables CSRF and allows requests to pass through to backend services)
│   │   └── ApiGatewayApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
|
├── common-module/                   # Shared components
│   ├── src/main/java/com/hostelgrid/common/
│   │   ├── exception/               # Global exception handling
│   │   ├── response/                # Common response DTOs
│   │   └── security/                # JWT utilities
│   └── pom.xml
|
├── auth-service/                    # Authentication microservice
│   ├── src/main/java/com/hostelgrid/authservice/
│   │   ├── config/                  # Security & JWT configuration
│   │   ├── controller/              # REST controllers
│   │   ├── dto/                     # Data transfer objects
│   │   ├── model/                   # JPA entities
│   │   ├── repository/              # Data access layer
│   │   ├── security/                # Custom security components
│   │   └── AuthServiceApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
|
├── pom.xml                          # Parent POM
├── README.md
```

## API Endpoints

### Via API Gateway (Port 8080)
- `POST http://localhost:8080/api/v1/auth/login` - User authentication
- `POST http://localhost:8080/api/v1/auth/register` - User registration
- `GET http://localhost:8080/api/auth-service/**` - Dynamic routing to auth-service

### Health Monitoring
- Auth Service: `http://localhost:8081/actuator/health`
- API Gateway: `http://localhost:8080/actuator/health`
- Service Registry: `http://localhost:8761/actuator/health`
- Via Gateway: `http://localhost:8080/api/auth-service/actuator/health`

### Additional Actuator Endpoints
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/gateway` (API Gateway only) - Gateway routes info

### Direct Access
- Auth Service: `http://localhost:8081/api/v1/auth/**`
- Eureka Dashboard: `http://localhost:8761` (admin:Admin@123)

### Service Discovery
- All registered services automatically available via gateway
- Dynamic routing: `http://localhost:8080/api/{service-name}/**`