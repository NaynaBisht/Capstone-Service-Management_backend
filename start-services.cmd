@echo off
echo  Starting Capstone Service Management App

echo Starting Eureka Server...
start "Eureka Server" java -jar eureka-server\target\eureka-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

timeout /t 8

echo Starting API Gateway...
start "API Gateway" java -jar api-gateway\target\api-gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

timeout /t 5

echo Starting Auth Service...
start "Auth Service" java -jar auth-service\target\auth-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

timeout /t 3

echo Starting Booking Service...
start "Booking Service" java -jar booking-service\target\booking-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

timeout /t 3

echo Starting Technician Service...
start "Technician Service" java -jar technician-service\target\technician-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

timeout /t 3

echo Starting Service Management Service...
start "Service Management Service" java -jar service-management-service\target\service-management-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

timeout /t 3

echo Starting Assignment Service...
start "Assignment Service" java -jar assignment-service\target\assignment-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

timeout /t 3

echo Starting Notification Service...
start "Notification Service" java -jar notification-service\target\notification-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=local

echo  All services started successfully!
