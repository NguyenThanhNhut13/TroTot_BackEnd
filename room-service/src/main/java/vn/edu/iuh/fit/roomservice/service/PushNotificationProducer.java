package vn.edu.iuh.fit.roomservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.roomservice.model.dto.request.PushNotificationRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "push-notification";

    public void sendNotification(PushNotificationRequest request) {
        try {
            String message = objectMapper.writeValueAsString(request);
            kafkaTemplate.send(TOPIC, message);
            log.info("Đã gửi push notification đến topic '{}': {}", TOPIC, message);
        } catch (Exception e) {
            log.error("Lỗi khi gửi push notification: {}", e.getMessage(), e);
        }
    }
}
