# Dockerfile in root folder
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY studbud/pom.xml .
COPY studbud/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
