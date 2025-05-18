# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app

# Install curl for healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy jar
COPY --from=build /app/target/*.jar payment-service.jar

EXPOSE 8040

# Direct command to run the jar without entrypoint script
CMD ["java", "-jar", "payment-service.jar"]