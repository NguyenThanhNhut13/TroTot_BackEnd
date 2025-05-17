#!/bin/sh

echo "Starting Config Server..."

# Optional: liệt kê secret để debug
echo "Contents of /etc/secrets:"
ls /etc/secrets

# Đặt biến môi trường để Spring Boot đọc từ file config & cert
export SPRING_CONFIG_ADDITIONAL_LOCATION=file:/etc/secrets/

# Khởi động Spring Boot app
exec java -jar config-server.jar