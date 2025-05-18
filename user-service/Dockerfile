# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app

# Copy jar
COPY --from=build /app/target/*.jar user-service.jar

EXPOSE 8090

# Direct command to run the jar without entrypoint script
CMD ["java", "-jar", "user-service.jar"]