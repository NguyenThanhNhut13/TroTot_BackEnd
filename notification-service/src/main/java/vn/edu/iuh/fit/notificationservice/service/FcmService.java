package vn.edu.iuh.fit.notificationservice.service;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.notificationservice.dto.PushNotificationRequest;

@Service
@Slf4j
public class FcmService {

    public void sendPushNotification(PushNotificationRequest request) {
        try {
            Message message = Message.builder()
                    .setToken(request.getTargetToken())
                    .setNotification(Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getMessage())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Push notification sent successfully: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending push notification: {}", e.getMessage(), e);
        }
    }
}
