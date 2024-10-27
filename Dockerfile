# Stage 1: Construiește fișierul JAR
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Creează imaginea finală
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/user-api-0.0.1-SNAPSHOT.jar user-service.jar

# Expune portul specific pentru User API
EXPOSE 8081

# Comanda de rulare a aplicației Spring Boot
ENTRYPOINT ["java", "-jar", "user-service.jar"]