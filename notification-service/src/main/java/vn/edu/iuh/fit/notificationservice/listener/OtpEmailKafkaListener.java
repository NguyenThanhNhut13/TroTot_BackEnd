package vn.edu.iuh.fit.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.notificationservice.dto.OtpEmailRequest;
import vn.edu.iuh.fit.notificationservice.service.EmailService;

@Component
@RequiredArgsConstructor
public class OtpEmailKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(OtpEmailKafkaListener.class);
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "otp-email", groupId = "notification-service")
    public void listen(String message) {
        try {
            OtpEmailRequest request = objectMapper.readValue(message, OtpEmailRequest.class);
            log.info("Nhận được yêu cầu gửi OTP tới: {}", request.getToEmail());
            emailService.sendOtpEmail(request.getToEmail(), request.getOtp());
        } catch (Exception e) {
            log.error("Lỗi khi xử lý message từ Kafka: {}", e.getMessage(), e);
        }
    }
}