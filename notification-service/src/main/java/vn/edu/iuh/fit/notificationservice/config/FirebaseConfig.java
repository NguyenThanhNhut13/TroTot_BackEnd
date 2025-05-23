package vn.edu.iuh.fit.notificationservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() {
        try (InputStream serviceAccount = getClass().getResourceAsStream("/tro-tot-443-firebase-adminsdk-fbsvc-14f6750dd1.json")) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase đã được khởi tạo thành công.");
            }
        } catch (IOException e) {
            log.error("Không thể khởi tạo Firebase: {}", e.getMessage(), e);
            throw new IllegalStateException("Cannot initialize Firebase", e);
        }
    }
}
