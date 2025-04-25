package vn.edu.iuh.fit.addressservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.entity.Coordinates;
import vn.edu.iuh.fit.addressservice.service.GeocodingService;
import vn.edu.iuh.fit.addressservice.util.BaseResponse;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/geocode")
@RequiredArgsConstructor
@Tag(name = "Geocoding", description = "API chuyển đổi giữa địa chỉ và tọa độ")
public class GeocodingController {

    private final GeocodingService geocodingService;

    @Operation(summary = "Forward Geocoding", description = "Chuyển địa chỉ thành tọa độ (latitude, longitude)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tọa độ được trả về thành công",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tọa độ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/forward")
    public ResponseEntity<BaseResponse<Coordinates>> forwardGeocode(@RequestParam String address) {
        Optional<Coordinates> result = geocodingService.forwardGeocode(address);
        return result.map(coords -> ResponseEntity.ok(BaseResponse.ok(coords)))
                .orElseGet(() -> ResponseEntity.status(404).body(BaseResponse.error("Address not found")));
    }


    @Operation(summary = "Reverse Geocoding", description = "Chuyển tọa độ (latitude, longitude) thành địa chỉ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Địa chỉ được trả về thành công",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/reverse")
    public ResponseEntity<BaseResponse<String>> reverseGeocode(@RequestParam double latitude, @RequestParam double longitude) {
        Optional<String> result = geocodingService.reverseGeocode(latitude, longitude);
        return result.map(address -> ResponseEntity.ok(BaseResponse.ok(address)))
                .orElseGet(() -> ResponseEntity.status(404).body(BaseResponse.error("Coordinates not found")));
    }
}
