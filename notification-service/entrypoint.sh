#!/bin/bash

echo "Starting Notification Service..."

DEFAULT_CRED_PATH="/etc/secrets/tro-tot-443-firebase-adminsdk-fbsvc-14f6750dd1.json"

export GOOGLE_APPLICATION_CREDENTIALS="${GOOGLE_APPLICATION_CREDENTIALS:-$DEFAULT_CRED_PATH}"

if [ ! -f "$GOOGLE_APPLICATION_CREDENTIALS" ]; then
  echo "Không tìm thấy Firebase credential tại: $GOOGLE_APPLICATION_CREDENTIALS"
  exit 1
fi

echo "Đang sử dụng Firebase credentials từ: $GOOGLE_APPLICATION_CREDENTIALS"

exec java ${JAVA_OPTS} -jar notification-service.jar "$@"
