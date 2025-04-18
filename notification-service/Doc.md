┌───────────────┐                      ┌─────────────────────┐
│   Người dùng  │                      │  Email (Gmail SMTP) │
└──────┬────────┘                      └─────────────────────┘
│
▼
┌────────────────────┐
│   auth-service     │
│(KafkaProducerService)│
└────────┬───────────┘
│ (send OTP JSON)
▼
┌────────────────────────────┐
│     Kafka Broker           │
│    (Topic: "otp-email")    │
└────────┬───────────────────┘
│ (auto push message)
▼
┌────────────────────────────┐
│ notification-service       │
│ (OtpEmailKafkaListener)    │
└────────┬───────────────────┘
│
▼
┌─────────────────────┐
│  EmailService        │
└────────┬────────────┘
│ (SMTP gửi mail)
▼
┌────────────────────┐
│ Người nhận (Email) │
└────────────────────┘
