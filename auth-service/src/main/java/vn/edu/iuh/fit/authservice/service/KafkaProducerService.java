package vn.edu.iuh.fit.authservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.authservice.model.dto.request.OtpEmailRequest;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            OtpEmailRequest request = new OtpEmailRequest(toEmail, otp);
            String json = objectMapper.writeValueAsString(request);
            kafkaTemplate.send("otp-email", json);
            log.info("Gửi OTP qua Kafka thành công: {}", json);
        } catch (Exception e) {
            log.error("Gửi OTP qua Kafka thất bại: {}", e.getMessage(), e);
        }
    }
}