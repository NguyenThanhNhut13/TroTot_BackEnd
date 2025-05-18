#!/bin/bash
# Run this script to generate base64 encoded certificates for Render

# Client keystore
echo "Converting client-keystore.p12 to base64..."
base64 ./config-server/src/main/resources/kafka-cert/client-keystore.p12 > client-keystore.b64
echo "KAFKA_SSL_KEYSTORE_CONTENT=$(cat client-keystore.b64)"

# Client truststore
echo "Converting client-truststore.p12 to base64..."
base64 ./config-server/src/main/resources/kafka-cert/client-truststore.p12 > client-truststore.b64
echo "KAFKA_SSL_TRUSTSTORE_CONTENT=$(cat client-truststore.b64)"

echo "Done! Add these environment variables to your Render deployment."
