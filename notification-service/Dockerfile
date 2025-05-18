# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app

COPY --from=build /app/target/*.jar notification-service.jar

COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

EXPOSE 8020

ENTRYPOINT ["./entrypoint.sh"]
