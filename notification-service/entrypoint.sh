#!/bin/sh

echo "Starting Notification Service..."

# Log Render environment
if [ "${RENDER}" = "true" ]; then
  echo "Running in Render environment"
else
  echo "Running in non-Render environment"
fi

# Log Firebase credentials path
if [ -f "${GOOGLE_APPLICATION_CREDENTIALS}" ]; then
  echo "Using Firebase credentials from ${GOOGLE_APPLICATION_CREDENTIALS}"
else
  echo "WARNING: Firebase credentials not found at ${GOOGLE_APPLICATION_CREDENTIALS}"
fi

# Log Kafka certificates
if [ -f "/etc/kafka/certs/client-keystore.p12" ] && [ -f "/etc/kafka/certs/client-truststore.p12" ]; then
  echo "Using Kafka certificates from /etc/kafka/certs"
else
  echo "No mounted Kafka certificates found - will fetch from Config Server"
fi

# Start the application
exec java ${JAVA_OPTS} -jar notification-service.jar