package vn.edu.iuh.fit.addressservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.service.GeocodingService;
import vn.edu.iuh.fit.addressservice.util.ApiResponse;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/geocode")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping("/forward")
    public ResponseEntity<?> forwardGeocode(@RequestParam String address) {
        Optional<double[]> result = geocodingService.forwardGeocode(address);
        return result.map(coords -> ResponseEntity.ok().body(
                        new ApiResponse<>(200, "Success", coords)))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(404, "Address not found", null)));
    }

    @GetMapping("/reverse")
    public ResponseEntity<?> reverseGeocode(@RequestParam double latitude, @RequestParam double longitude) {
        Optional<String> result = geocodingService.reverseGeocode(latitude, longitude);
        return result.map(address -> ResponseEntity.ok().body(
                        new ApiResponse<>(200, "Success", address)))
                .orElseGet(() -> ResponseEntity.status(404).body(new ApiResponse<>(404, "Coordinates not found", null)));
    }
}
