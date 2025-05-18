# Build stage
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app

# Install curl and base64 for certificate handling
RUN apt-get update && apt-get install -y curl coreutils && rm -rf /var/lib/apt/lists/*

# Copy jar
COPY --from=build /app/target/*.jar notification-service.jar

# Copy Firebase credentials
COPY --from=build /app/src/main/resources/tro-tot-443-firebase-adminsdk-fbsvc-14f6750dd1.json /etc/secrets/firebase.json

# Create directories for certificates
RUN mkdir -p /app/certs /etc/kafka/certs

# Copy entrypoint script
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh

EXPOSE 8020
ENTRYPOINT ["./entrypoint.sh"]