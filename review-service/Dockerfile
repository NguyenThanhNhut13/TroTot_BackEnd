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
COPY --from=build /app/target/*.jar review-service.jar

EXPOSE 8030

# Direct command to run the jar without entrypoint script
CMD ["java", "-jar", "review-service.jar"]