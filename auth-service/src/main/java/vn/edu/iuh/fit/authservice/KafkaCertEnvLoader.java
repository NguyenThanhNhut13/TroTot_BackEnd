package vn.edu.iuh.fit.authservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class KafkaCertEnvLoader implements EnvironmentPostProcessor {


    private File copyToTempFile(String classpathPath) throws Exception {
        ClassPathResource resource = new ClassPathResource(classpathPath);
        if (!resource.exists()) {
            throw new IllegalArgumentException("Classpath resource not found: " + classpathPath);
        }

        File tempFile = File.createTempFile("cert-", ".p12");
        tempFile.deleteOnExit();

        try (InputStream in = resource.getInputStream(); FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            File keystore = copyToTempFile("kafka-cert/client-keystore.p12");
            File truststore = copyToTempFile("kafka-cert/client-truststore.p12");

            System.setProperty("SSL_KEYSTORE_PATH", keystore.getAbsolutePath());
            System.setProperty("SSL_TRUSTSTORE_PATH", truststore.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SSL certs from classpath", e);
        }
    }
}
