package vn.edu.iuh.fit.configserver.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/certs")
public class KafkaCertController {
    private static final String CERTS_DIR = "/etc/kafka/certs";
    private static final String CLASSPATH_DIR = "kafka-cert/";

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getCert(@PathVariable String filename) {
        Resource resource;
        String contentType;

        // Check mounted directory first (Docker Compose/Render)
        File file = new File(CERTS_DIR, filename);
        if (file.exists()) {
            resource = new FileSystemResource(file);
        } else {
            // Fallback to classpath (local)
            resource = new ClassPathResource(CLASSPATH_DIR + filename);
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
        }

        // Set content type based on file extension
        contentType = filename.endsWith(".b64") ? MediaType.TEXT_PLAIN_VALUE : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
