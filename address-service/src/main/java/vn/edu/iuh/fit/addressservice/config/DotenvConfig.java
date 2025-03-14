package vn.edu.iuh.fit.addressservice.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DotenvConfig {

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources") // Load từ thư mục `resources/`
                .filename(".env") // Tên file `.env`
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> {
            String key = entry.getKey();
            String value = entry.getValue();
            System.setProperty(key, value);  // Lưu vào System properties
            log.info("✅ Loaded env: {} = {}", key, value);
        });
    }
}
