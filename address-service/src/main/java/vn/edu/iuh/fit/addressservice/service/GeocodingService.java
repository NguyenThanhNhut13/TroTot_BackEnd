package vn.edu.iuh.fit.addressservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org";

    // Forward Geocoding: Địa chỉ -> Tọa độ
    @Cacheable(value = "geocodingCache", key = "#address")
    public Optional<double[]> forwardGeocode(String address) {
        String url = NOMINATIM_BASE_URL + "/search?format=json&q=" + address + "&limit=1";
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            if (root.isArray() && root.size() > 0) {
                JsonNode location = root.get(0);
                return Optional.of(new double[]{
                        location.get("lat").asDouble(),
                        location.get("lon").asDouble()
                });
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi OpenStreetMap API: " + e.getMessage());
        }
        return Optional.empty();
    }

    // Reverse Geocoding: Tọa độ -> Địa chỉ
    @Cacheable(value = "geocodingCache", key = "#latitude + ',' + #longitude")
    public Optional<String> reverseGeocode(double latitude, double longitude) {
        String url = NOMINATIM_BASE_URL + "/reverse?format=json&lat=" + latitude + "&lon=" + longitude;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            if (root.has("display_name")) {
                return Optional.of(root.get("display_name").asText());
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gọi OpenStreetMap API: " + e.getMessage());
        }
        return Optional.empty();
    }
}
