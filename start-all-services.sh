#!/bin/bash
echo "Starting HostelGrid Microservices..."

echo "Building all services..."
mvn clean install -DskipTests

echo "Starting Service Registry..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && mvn spring-boot:run -pl service-registry"'
sleep 30

echo "Starting API Gateway..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && mvn spring-boot:run -pl api-gateway"'
sleep 15

echo "Starting Auth Service..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && mvn spring-boot:run -pl auth-service"'
sleep 10

echo "Starting Notification Service..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && mvn spring-boot:run -pl notification-service"'
sleep 10

echo "Starting Hostel Service..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && mvn spring-boot:run -pl hostel-service"'
sleep 10

echo "Starting Student Service..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && mvn spring-boot:run -pl student-service"'
sleep 10

echo "Starting Billing Service..."
osascript -e 'tell app "Terminal" to do script "cd '$(pwd)' && mvn spring-boot:run -pl billing-service"'

echo "All services are starting..."
echo "Check Eureka Dashboard: http://localhost:8761"
echo "API Gateway: http://localhost:8080"
echo "Press any key to continue..."
read -n 1