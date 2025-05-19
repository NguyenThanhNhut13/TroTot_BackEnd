#!/bin/sh

echo "Starting Config Server..."

# Log contents of /etc/secrets for debugging
echo "Contents of /etc/secrets:"
ls -l /etc/secrets 2>/dev/null || echo "Directory /etc/secrets is empty or does not exist"

# Log contents of /etc/kafka/certs for debugging
echo "Contents of /etc/kafka/certs:"
ls -l /etc/kafka/certs 2>/dev/null || echo "Directory /etc/kafka/certs is empty or does not exist"

# Set environment variable for Spring Boot to read config from /etc/secrets
export SPRING_CONFIG_ADDITIONAL_LOCATION=file:/etc/secrets/

# Start the Spring Boot application
exec java -jar config-server.jar