package vn.edu.iuh.fit.notificationservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.edu.iuh.fit.notificationservice.dto.PushNotificationRequest;
import vn.edu.iuh.fit.notificationservice.service.FcmService;

@Component
@RequiredArgsConstructor
@Slf4j
public class PushNotificationKafkaListener {

    private final ObjectMapper objectMapper;
    private final FcmService fcmService;

    @KafkaListener(topics = "push-notification", groupId = "notification-service")
    public void listen(String message) {
        try {
            PushNotificationRequest request = objectMapper.readValue(message, PushNotificationRequest.class);
            fcmService.sendPushNotification(request);
        } catch (Exception e) {
            log.error("Error while processing push-notification message: {} \nError: {}", message, e.getMessage(), e);
        }
    }
}
