# Hostelgrid Backend

A comprehensive Spring Boot microservices backend for complete hostel management system with JWT authentication, event-driven architecture, and modular design.

## Architecture

- **Multi-module Maven project** with shared common components
- **Microservices architecture** for scalability and maintainability
- **Event-driven communication** using Apache Kafka
- **JWT-based authentication** for secure API access
- **Spring Security** integration for role-based authorization
- **API Gateway** for centralized routing and load balancing

## Current Modules (Implemented)

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

### notification-service
Event-driven notification microservice for email and messaging.

**Features:**
- Kafka event consumption
- Email notifications (welcome emails)
- Message tracking and retry logic
- Async processing with idempotency

### common-module
Shared utilities and components across all microservices.

**Components:**
- JWT token provider and validation
- Common security configurations
- Shared DTOs and utilities
- Event classes for Kafka messaging

## Planned Microservices Architecture

### 🏢 2. Hostel Management Service
**Core hostel details management**
- Manage Hostels (Add/Edit/Delete)
- Manage Blocks/Wings/Floors
- Define Room Types (Single, Double, Dormitory)
- Room Capacity & Availability
- Facilities Management (WiFi, AC, Laundry, Mess)

### 🛏️ 3. Room & Bed Allocation Service
**Manages room assignments**
- Allocate bed/room to students
- Re-allocation on request
- Track current occupancy
- Auto-allocation based on preferences
- Occupancy Rate Dashboard

### 👩🎓 4. Student Management Service
**Centralized student data handling**
- Student Profiles (personal & academic)
- Emergency Contacts & Guardian details
- Document Upload (ID Proof, Certificates)
- Attendance integration
- Hostel Fee Tracking

### 💰 5. Billing & Payments Service
**Handles hostel-related payments**
- Hostel Fee management (monthly/semester)
- Online Payments (UPI, Card, Net Banking)
- Payment Gateway Integration (Razorpay, Stripe)
- Auto-generated Invoices & Receipts
- Fine Management & Refund Processing

### 🍴 6. Mess & Food Management Service
**Food plans and meal schedules**
- Student Meal Plans (Veg/Non-Veg, Special Diet)
- Daily/Weekly Menu Management
- Mess Attendance (swipe card/QR)
- Food Quality Feedback System
- Waste tracking & cost optimization

### 📅 7. Event & Activity Management Service
**Student engagement platform**
- Hostel Events (Cultural, Sports, Tech)
- Student Club/Committee Management
- Event Registration & Attendance
- Announcements/Notice Board

### 🛠️ 8. Maintenance & Complaint Service
**Issue management system**
- Complaint Registration (Plumbing, Electrical, Cleaning)
- Complaint Assignment to staff
- Status Tracking (Open, In-progress, Resolved)
- Resolution Feedback
- Maintenance History per room

### 🛡️ 9. Security & Visitor Management Service
**Safety and access control**
- Student Entry/Exit Logs (RFID/QR/Face Recognition)
- Visitor Registration & Pass System
- Emergency Alerts (Fire, Medical)
- Blacklist/Restricted visitors

### 📚 10. Learning & Development Service
**Educational enhancement**
- Online Workshops & Training
- Event Booking & Certificate Generation
- Learning Material Repository

### 📊 11. Reporting & Analytics Service
**Cross-service dashboards**
- Occupancy Reports
- Financial Reports
- Mess Usage Reports
- Complaint & Maintenance Statistics
- Predictive Analytics

### 🏛️ 12. Admin Service
**Super admin functions**
- Create/Edit/Delete Hostels
- Manage Wardens/Staff accounts
- Define Hostel Policies & Rules
- Global Announcements

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
- **Apache Kafka** - Event streaming platform
- **MySQL** - Database
- **JavaMail** - Email sending
- **Lombok** - Code generation
- **Maven** - Dependency management

## Future Tech Integrations

- **Elasticsearch** - Fast searching across students, rooms, complaints
- **GraphQL Gateway** - Efficient data fetching across microservices
- **AI Module** - Predictive analysis (room demand, mess wastage)
- **Redis** - Caching and session management
- **Docker** - Containerization for deployment
- **Kubernetes** - Container orchestration

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
   - Create MySQL databases: `hostelgrid_auth_db`, `hostelgrid_notification_db`
   - Update credentials in respective service application.yml files



3. **Setup Apache Kafka & Zookeeper:**
   
   **Windows:**
   ```bash
   # Download Kafka
   # Visit: https://kafka.apache.org/downloads
   # Download kafka_2.12-3.9.1.tgz and extract to C:\kafka_2.12-3.9.1
   
   # Start Zookeeper (Terminal 1)
   cd C:\kafka_2.12-3.9.1
   bin\windows\zookeeper-server-start.bat config\zookeeper.properties
   
   # Start Kafka Server (Terminal 2)
   bin\windows\kafka-server-start.bat config\server.properties
   
   # Verify installation (Terminal 3)
   bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
   ```
   
   **macOS (using Homebrew):**
   ```bash
   # Install Kafka (includes Zookeeper)
   brew install kafka
   
   # Start Zookeeper
   brew services start zookeeper
   
   # Start Kafka
   brew services start kafka
   
   # Verify installation
   kafka-topics --list --bootstrap-server localhost:9092
   ```
   
   **Linux (Ubuntu/Debian):**
   ```bash
   # Download and extract Kafka
   wget https://downloads.apache.org/kafka/2.8.2/kafka_2.12-2.8.2.tgz
   tar -xzf kafka_2.12-2.8.2.tgz
   cd kafka_2.12-2.8.2
   
   # Start Zookeeper (Terminal 1)
   bin/zookeeper-server-start.sh config/zookeeper.properties
   
   # Start Kafka Server (Terminal 2)
   bin/kafka-server-start.sh config/server.properties
   
   # Verify installation (Terminal 3)
   bin/kafka-topics.sh --list --bootstrap-server localhost:9092
   ```
   
   **Docker (Alternative - All Platforms):**
   ```bash
   # Create docker-compose.yml
   version: '3'
   services:
     zookeeper:
       image: confluentinc/cp-zookeeper:latest
       environment:
         ZOOKEEPER_CLIENT_PORT: 2181
       ports:
         - "2181:2181"
     
     kafka:
       image: confluentinc/cp-kafka:latest
       depends_on:
         - zookeeper
       ports:
         - "9092:9092"
       environment:
         KAFKA_BROKER_ID: 1
         KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
         KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
         KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
   
   # Start with Docker Compose
   docker-compose up -d
   ```
   
   **Topics Configuration:**
   - Topics are auto-created when first message is sent
   - Current topics: `user-registration`
   - Kafka runs on: `localhost:9092`
   - Zookeeper runs on: `localhost:2181`



4. **Build and Run:**
   
   **Option 1: Use startup script (Recommended)**
   ```bash
   # Windows
   start-all-services.bat
   stop-all-services.bat
   
   # macOS/Linux
   chmod +x start-all-services.sh stop-all-services.sh
   ./start-all-services.sh
   ./stop-all-services.sh
   ```
   
   **Option 2: Manual start (Individual services)**
   ```bash
   mvn clean install
   
   # Start services in order:
   mvn spring-boot:run -pl service-registry      # Port 8761
   mvn spring-boot:run -pl api-gateway           # Port 8080
   mvn spring-boot:run -pl auth-service          # Port 8081
   mvn spring-boot:run -pl notification-service  # Port 8082
   mvn spring-boot:run -pl hostel-service        # Port 8083
   mvn spring-boot:run -pl student-service       # Port 8084
   mvn spring-boot:run -pl billing-service       # Port 8085
   ```
   
   **Note about `-pl` flag:**
   - `-pl` stands for "projects list" in Maven multi-module projects
   - It targets a specific module from the parent directory
   - Without `-pl`: You'd need to `cd` into each service directory
   - With `-pl`: Run all services from the root project directory
   - Example: `mvn spring-boot:run -pl billing-service` runs only the billing service



5. **Access Points:**
   - API Gateway: http://localhost:8080
   - Eureka Dashboard: http://localhost:8761 (admin:Admin@123)
   - Auth Service: http://localhost:8081
   - Notification Service: http://localhost:8082
   - Hostel Service: http://localhost:8083
   - Student Service: http://localhost:8084
   - Billing Service: http://localhost:8085



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
│   │   ├── config/SecurityConfig.java       # Gateway security config
│   │   ├── filter/                  # Authentication filters
│   │   │    └── JwtAuthenticationFilter.java # JWT validation & user context forwarding
│   │   └── ApiGatewayApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
|
├── common-module/                   # Shared components
│   ├── src/main/java/com/hostelgrid/common/
│   │   ├── events/                  # Kafka event classes
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
│   │   ├── service/                 # Business logic & Kafka producers
│   │   └── AuthServiceApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
|
├── notification-service/            # Notification microservice
│   ├── src/main/java/com/hostelgrid/notification/
│   │   ├── listener/                # Kafka event listeners
│   │   ├── model/                   # JPA entities
│   │   ├── repository/              # Data access layer
│   │   ├── service/                 # Email & notification services
│   │   └── NotificationServiceApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
|
├── hostel-service/                  # Hostel management microservice
│   ├── src/main/java/com/hostelgrid/hostelservice/
│   │   ├── config/                  # Security configuration
│   │   ├── controller/              # REST controllers
│   │   ├── dto/                     # Data transfer objects
│   │   ├── filter/                  # User context filter for every api
│   │   ├── model/                   # JPA entities
│   │   ├── repository/              # Data access layer
│   │   ├── service/                 # Business logic
│   │   └── HostelServiceApplication.java
│   ├── src/main/resources/application.yml
│   └── pom.xml
|
├── start-all-services.bat           # Windows startup script
├── stop-all-services.bat            # Windows stop script
├── start-all-services.sh            # macOS/Linux startup script
├── stop-all-services.sh             # macOS/Linux stop script
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
- Notification Service: `http://localhost:8082/actuator/health`
- API Gateway: `http://localhost:8080/actuator/health`
- Service Registry: `http://localhost:8761/actuator/health`
- Via Gateway: `http://localhost:8080/api/{service-name}/actuator/health`

### Additional Actuator Endpoints
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics
- `/actuator/gateway` (API Gateway only) - Gateway routes info


### Direct Access
- Auth Service: `http://localhost:8081/api/v1/auth/**`
- Eureka Dashboard: `http://localhost:8761` (admin:Admin@123)




## Event-Driven Architecture Overview

### Event-Driven Architecture
- **Current Topics:** `user-registration`
- **Planned Topics:** `room-allocation`, `payment-success`, `complaint-created`, `maintenance-request`
- **Event Flow:** User registration → Kafka event → Welcome email
- **Features:** Async processing, retry logic, idempotency
- **Benefits:** Loose coupling, scalability, fault tolerance

### Kafka Management Commands

**Create Topic:**
```bash
# Windows
bin\windows\kafka-topics.bat --create --topic user-registration --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# macOS/Linux
kafka-topics --create --topic user-registration --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

**List Topics:**
```bash
# Windows
bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

# macOS/Linux
kafka-topics --list --bootstrap-server localhost:9092
```

**Delete Topic:**
```bash
# Windows
bin\windows\kafka-topics.bat --delete --topic user-registration --bootstrap-server localhost:9092

# macOS/Linux
kafka-topics --delete --topic user-registration --bootstrap-server localhost:9092
```

**Monitor Messages:**
```bash
# Windows
bin\windows\kafka-console-consumer.bat --topic user-registration --from-beginning --bootstrap-server localhost:9092

# macOS/Linux
kafka-console-consumer --topic user-registration --from-beginning --bootstrap-server localhost:9092
```

## Security Architecture

### API Gateway Authentication
- **JWT Validation**: API Gateway validates JWT tokens for protected endpoints
- **User Context Forwarding**: Extracts user info and forwards via headers:
  - `X-User-Id` - User ID from token
  - `X-User-Email` - User email from token
  - `X-User-Role` - User role from token
  - `X-Authenticated` - Authentication status

### Public Endpoints (No Authentication)
- `/api/v1/auth/**` - Authentication endpoints
- `/api/v1/hostels/active` - Active hostels
- `/api/v1/hostels/search` - Search hostels
- `/actuator/**` - Health monitoring
- `/swagger-ui/**` - API documentation

### Protected Endpoints (JWT Required)
- All other endpoints require valid JWT token in `Authorization: Bearer <token>` header

### Role-Based Access Control
- **ADMIN**: Full system access (all CRUD operations)
- **OWNER**: Hostel management (create, update hostels)
- **WARDEN**: Hostel operations (view, manage rooms)
- **STUDENT/USER**: Basic access (view hostels, create bookings)

### Method-Level Security
- Individual services use `@PreAuthorize` annotations
- Fine-grained permission control using authorities
- User context extracted from API Gateway headers

### Authentication Flow
1. Client sends request with JWT token to API Gateway
2. API Gateway validates JWT and extracts user context
3. Gateway forwards request with user headers to target service
4. Target service extracts user context and applies method-level security
5. Service processes request based on user permissions

## Service Discovery and Routing

### Service Discovery
- All registered services automatically available via gateway
- Dynamic routing: `http://localhost:8080/api/{service-name}/**`