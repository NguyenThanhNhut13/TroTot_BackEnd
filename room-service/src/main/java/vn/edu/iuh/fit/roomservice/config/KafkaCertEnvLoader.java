package vn.edu.iuh.fit.roomservice.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Environment post-processor that loads Kafka certificate files
 * either from the filesystem or from a config server.
 */
public class KafkaCertEnvLoader implements EnvironmentPostProcessor {

    private static final String CONFIG_SERVER_BASE_URL = System.getenv("CONFIG_SERVER_URL") != null ?
            System.getenv("CONFIG_SERVER_URL") : "http://localhost:8888";
    private static final String KEYSTORE_PATH = "/etc/kafka/certs/client-keystore.p12";
    private static final String TRUSTSTORE_PATH = "/etc/kafka/certs/client-truststore.p12";
    private static final boolean IS_RENDER = "true".equalsIgnoreCase(System.getenv("RENDER"));

    /**
     * Downloads certificate file from config server and processes it as needed.
     *
     * @param filename The name of the certificate file to download
     * @param decodeBase64 Whether to decode base64-encoded certificate content
     * @return The downloaded certificate file
     * @throws IOException If there's an error downloading or processing the certificate
     */
    private File downloadAndProcessCert(String filename, boolean decodeBase64) throws IOException {
        String url = CONFIG_SERVER_BASE_URL + "/certs/" + filename;
        InputStream in = new URL(url).openStream();
        File tempFile = File.createTempFile("cert-", ".p12");
        tempFile.deleteOnExit();

        if (decodeBase64) {
            // Read Base64 content and decode to .p12
            String base64Content = new String(in.readAllBytes()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            Files.write(tempFile.toPath(), decodedBytes);
        } else {
            // Write binary .p12 content directly
            try (OutputStream out = new FileOutputStream(tempFile)) {
                in.transferTo(out);
            }
        }

        return tempFile;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            File keystore;
            File truststore;

            // Check if certificates are mounted (Docker Compose or local)
            if (new File(KEYSTORE_PATH).exists() && new File(TRUSTSTORE_PATH).exists()) {
                keystore = new File(KEYSTORE_PATH);
                truststore = new File(TRUSTSTORE_PATH);
            } else {
                // Download from config server
                String keystoreFilename = IS_RENDER ? "client-keystore.b64" : "client-keystore.p12";
                String truststoreFilename = IS_RENDER ? "client-truststore.b64" : "client-truststore.p12";

                keystore = downloadAndProcessCert(keystoreFilename, IS_RENDER);
                truststore = downloadAndProcessCert(truststoreFilename, IS_RENDER);
            }

            Map<String, Object> props = new HashMap<>();
            props.put("SSL_KEYSTORE_PATH", keystore.getAbsolutePath());
            props.put("SSL_TRUSTSTORE_PATH", truststore.getAbsolutePath());

            environment.getPropertySources().addFirst(new MapPropertySource("ssl-cert-paths", props));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load certificates", e);
        }
    }
}