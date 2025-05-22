# detect-changes.ps1
$changedFiles = git diff --name-only HEAD^ HEAD
$services = @('address-service', 'api-gateway', 'auth-service', 'discovery', 'media-service', 'notification-service', 'payment-service', 'recommendation-service', 'report-service', 'review-service', 'room-service', 'user-service')
$changedServices = @()
foreach ($file in $changedFiles) {
  foreach ($service in $services) {
    if ($file -like "$service/*") {
      $changedServices += $service
    }
  }
}
$changedServices | Select-Object -Unique