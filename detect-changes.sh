#!/bin/bash
changed_files=$(git diff --name-only HEAD^ HEAD)
services=()
for service in address-service api-gateway auth-service discovery media-service notification-service payment-service recommendation-service report-service review-service room-service user-service; do
  if echo "$changed_files" | grep -q "^$service/"; then
    services+=("$service")
  fi
done
echo "${services[@]}"