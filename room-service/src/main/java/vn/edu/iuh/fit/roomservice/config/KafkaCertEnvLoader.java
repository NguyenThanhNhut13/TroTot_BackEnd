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
import java.util.HashMap;
import java.util.Map;

public class KafkaCertEnvLoader implements EnvironmentPostProcessor {
    private static final String CONFIG_SERVER_BASE_URL = System.getenv("CONFIG_SERVER_URL") != null 
        ? System.getenv("CONFIG_SERVER_URL") 
        : "http://localhost:8888";

    private File downloadCert(String filename) throws IOException {
        String url = CONFIG_SERVER_BASE_URL + "/certs/" + filename;
        System.out.println("Attempting to download certificate from: " + url);
        InputStream in = new URL(url).openStream();
        File tempFile = File.createTempFile("cert-", ".p12");
        tempFile.deleteOnExit();

        try (OutputStream out = new FileOutputStream(tempFile)) {
            in.transferTo(out);
        }
        System.out.println("Certificate downloaded successfully to: " + tempFile.getAbsolutePath());

        return tempFile;
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Check if environment variables are already set by the entrypoint script
        String keystorePath = System.getenv("SSL_KEYSTORE_PATH");
        String truststorePath = System.getenv("SSL_TRUSTSTORE_PATH");
        
        Map<String, Object> props = new HashMap<>();
        
        if (keystorePath != null && truststorePath != null) {
            System.out.println("Using pre-configured certificate paths:");
            System.out.println("  Keystore: " + keystorePath);
            System.out.println("  Truststore: " + truststorePath);
            
            // Use the existing paths
            props.put("SSL_KEYSTORE_PATH", keystorePath);
            props.put("SSL_TRUSTSTORE_PATH", truststorePath);
        } else {
            try {
                System.out.println("No certificate paths found in environment, downloading from config server");
                File keystore = downloadCert("client-keystore.p12");
                File truststore = downloadCert("client-truststore.p12");
                
                props.put("SSL_KEYSTORE_PATH", keystore.getAbsolutePath());
                props.put("SSL_TRUSTSTORE_PATH", truststore.getAbsolutePath());
            } catch (IOException e) {
                // Make it less fatal in Docker environments
                if ("docker".equals(System.getenv("SPRING_PROFILES_ACTIVE"))) {
                    System.err.println("WARNING: Failed to load certificates: " + e.getMessage());
                    return;
                } else {
                    throw new RuntimeException("Failed to load certs from config server", e);
                }
            }
        }
        
        environment.getPropertySources().addFirst(new MapPropertySource("ssl-cert-paths", props));
    }
}