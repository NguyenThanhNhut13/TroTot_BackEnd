package vn.edu.iuh.fit.notificationservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationRequest {
    private String targetToken; // FCM token
    private String title;
    private String message;
}
