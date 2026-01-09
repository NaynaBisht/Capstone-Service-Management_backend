FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8082

# ENTRYPOINT ["java","-jar","/app/app.jar"]
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]