package vn.edu.iuh.fit.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.notificationservice.dto.OtpEmailRequest;
import vn.edu.iuh.fit.notificationservice.service.EmailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpEmailKafkaListener {

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
