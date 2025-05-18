#!/bin/bash

echo "Starting Notification Service..."

# Đường dẫn mặc định tới file Firebase Admin SDK
DEFAULT_FIREBASE_CREDENTIAL="/var/render/secrets/tro-tot-443-firebase-adminsdk-fbsvc-14f6750dd1.json"

# Nếu biến không set thì fallback
GOOGLE_CRED_PATH="${GOOGLE_APPLICATION_CREDENTIALS:-$DEFAULT_FIREBASE_CREDENTIAL}"

# Kiểm tra và gán nếu file tồn tại
if [ -f "$GOOGLE_CRED_PATH" ]; then
    echo "Using Firebase credentials from $GOOGLE_CRED_PATH"
    export GOOGLE_APPLICATION_CREDENTIALS="$GOOGLE_CRED_PATH"
else
    echo "ERROR: Firebase credential file not found at $GOOGLE_CRED_PATH"
    exit 1
fi

# Chạy ứng dụng Spring Boot
exec java ${JAVA_OPTS} -jar notification-service.jar "$@"
