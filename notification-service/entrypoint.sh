#!/bin/sh

echo "Starting Notification Service..."

# Check for Firebase credentials
if [ -f "/etc/secrets/firebase.json" ]; then
  echo "Using Firebase credentials from mounted volume"
  export GOOGLE_APPLICATION_CREDENTIALS=/etc/secrets/firebase.json
else
  echo "WARNING: Firebase credentials not found"
fi

# Check for Kafka certificates
if [ -f "/etc/kafka/certs/client-keystore.p12" ] && [ -f "/etc/kafka/certs/client-truststore.p12" ]; then
  echo "Using Kafka certificates from mounted volume"
  export SSL_KEYSTORE_PATH=/etc/kafka/certs/client-keystore.p12
  export SSL_TRUSTSTORE_PATH=/etc/kafka/certs/client-truststore.p12
  echo "Using certificates from $SSL_KEYSTORE_PATH and $SSL_TRUSTSTORE_PATH"
else
  echo "No mounted Kafka certificates found - will try Config Server"
fi

# Start the application
exec java ${JAVA_OPTS} -jar notification-service.jar