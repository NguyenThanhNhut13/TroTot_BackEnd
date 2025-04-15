package vn.edu.iuh.fit.addressservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.entity.Coordinates;
import vn.edu.iuh.fit.addressservice.service.GeocodingService;
import vn.edu.iuh.fit.addressservice.util.BaseResponse;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/geocode")
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;

    @GetMapping("/forward")
    public ResponseEntity<BaseResponse<Coordinates>> forwardGeocode(@RequestParam String address) {
        Optional<Coordinates> result = geocodingService.forwardGeocode(address);
        return result.map(coords -> ResponseEntity.ok(BaseResponse.ok(coords)))
                .orElseGet(() -> ResponseEntity.status(404).body(BaseResponse.error("Address not found")));
    }


    @GetMapping("/reverse")
    public ResponseEntity<BaseResponse<String>> reverseGeocode(@RequestParam double latitude, @RequestParam double longitude) {
        Optional<String> result = geocodingService.reverseGeocode(latitude, longitude);
        return result.map(address -> ResponseEntity.ok(BaseResponse.ok(address)))
                .orElseGet(() -> ResponseEntity.status(404).body(BaseResponse.error("Coordinates not found")));
    }
}
