package vn.edu.iuh.fit.notificationservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);
    private static final String FIREBASE_JSON_PATH = "/etc/secrets/firebase.json";
    private static final String CLASSPATH_JSON = "/tro-tot-443-firebase-adminsdk-fbsvc-14f6750dd1.json";

    @PostConstruct
    public void initFirebase() {
        try {
            InputStream serviceAccount;

            // Check environment variable (Render or Docker)
            String envPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            if (envPath != null && !envPath.isBlank() && new File(envPath).exists()) {
                log.info("Initializing Firebase from env path: {}", envPath);
                serviceAccount = new FileInputStream(envPath);
            }
            // Check mounted volume (Docker/Render)
            else if (new File(FIREBASE_JSON_PATH).exists()) {
                log.info("Initializing Firebase from mounted volume: {}", FIREBASE_JSON_PATH);
                serviceAccount = new FileInputStream(FIREBASE_JSON_PATH);
            }
            // Fallback to classpath (local development)
            else {
                log.info("Initializing Firebase from classpath: {}", CLASSPATH_JSON);
                serviceAccount = getClass().getResourceAsStream(CLASSPATH_JSON);
                if (serviceAccount == null) {
                    throw new IllegalStateException("Firebase JSON not found in classpath or mounted volume");
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully");
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage(), e);
            throw new IllegalStateException("Cannot initialize Firebase", e);
        }
    }
}
