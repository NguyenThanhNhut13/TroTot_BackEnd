package vn.edu.iuh.fit.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class KafkaCertEnvLoader implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            File keystore = loadCert("kafka-cert/client-keystore.p12");
            File truststore = loadCert("kafka-cert/client-truststore.p12");

            Map<String, Object> props = new HashMap<>();
            props.put("SSL_KEYSTORE_PATH", keystore.getAbsolutePath());
            props.put("SSL_TRUSTSTORE_PATH", truststore.getAbsolutePath());

            environment.getPropertySources().addFirst(new MapPropertySource("ssl-cert-paths", props));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SSL certs from classpath", e);
        }
    }

    private File loadCert(String resourcePath) throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) throw new FileNotFoundException("Not found in classpath: " + resourcePath);
        File tempFile = File.createTempFile("cert-", ".p12");
        tempFile.deleteOnExit();
        try (OutputStream out = new FileOutputStream(tempFile)) {
            in.transferTo(out);
        }
        return tempFile;
    }
}
