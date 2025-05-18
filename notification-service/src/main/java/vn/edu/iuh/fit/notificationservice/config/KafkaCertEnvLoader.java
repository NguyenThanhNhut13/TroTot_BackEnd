package vn.edu.iuh.fit.notificationservice.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class KafkaCertEnvLoader implements EnvironmentPostProcessor {
    private static final String CONFIG_SERVER_BASE_URL = System.getenv("CONFIG_SERVER_URL") != null 
    ? System.getenv("CONFIG_SERVER_URL") 
    : "http://localhost:8888";

    private File downloadCert(String filename) throws IOException {
        String url = CONFIG_SERVER_BASE_URL + "/certs/" + filename;
        InputStream in = new URL(url).openStream();
        File tempFile = File.createTempFile("cert-", ".p12");
        tempFile.deleteOnExit();

        try (OutputStream out = new FileOutputStream(tempFile)) {
            in.transferTo(out);
        }

        return tempFile;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            File keystore = downloadCert("client-keystore.p12");
            File truststore = downloadCert("client-truststore.p12");

            Map<String, Object> props = new HashMap<>();
            props.put("SSL_KEYSTORE_PATH", keystore.getAbsolutePath());
            props.put("SSL_TRUSTSTORE_PATH", truststore.getAbsolutePath());

            environment.getPropertySources().addFirst(new MapPropertySource("ssl-cert-paths", props));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load certs from config server", e);
        }
    }
}
