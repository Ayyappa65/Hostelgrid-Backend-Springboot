@echo off
echo Starting HostelGrid Microservices...

echo Building all services...
call mvn clean install -DskipTests

echo Starting Service Registry...
start "Service Registry" cmd /k "mvn spring-boot:run -pl service-registry"
timeout /t 30

echo Starting API Gateway...
start "API Gateway" cmd /k "mvn spring-boot:run -pl api-gateway"
timeout /t 15

echo Starting Auth Service...
start "Auth Service" cmd /k "mvn spring-boot:run -pl auth-service"
timeout /t 10

echo Starting Notification Service...
start "Notification Service" cmd /k "mvn spring-boot:run -pl notification-service"
timeout /t 10

echo Starting Hostel Service...
start "Hostel Service" cmd /k "mvn spring-boot:run -pl hostel-service"
timeout /t 10

echo Starting Student Service...
start "Student Service" cmd /k "mvn spring-boot:run -pl student-service"
timeout /t 10

echo Starting Billing Service...
start "Billing Service" cmd /k "mvn spring-boot:run -pl billing-service"

echo All services are starting...
echo Check Eureka Dashboard: http://localhost:8761
echo API Gateway: http://localhost:8080
pause