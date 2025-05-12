package vn.edu.iuh.fit.addressservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.dto.AddressDTO;
import vn.edu.iuh.fit.addressservice.dto.AddressSummaryDTO;
import vn.edu.iuh.fit.addressservice.entity.Address;
import vn.edu.iuh.fit.addressservice.entity.Coordinates;
import vn.edu.iuh.fit.addressservice.service.AddressService;
import vn.edu.iuh.fit.addressservice.service.GeocodingService;
import vn.edu.iuh.fit.addressservice.util.BaseResponse;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Tag(name = "Address Controller", description = "Quản lý thông tin địa chỉ và xử lý tọa độ thông qua dịch vụ Geocoding")
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final GeocodingService geocodingService;
    private AtomicInteger callCounter = new AtomicInteger(0);


    @Operation(
            summary = "Thêm địa chỉ mới",
            description = "Thêm một địa chỉ mới vào hệ thống và tự động lấy tọa độ (latitude, longitude) từ dịch vụ Geocoding nếu có đủ thông tin."
    )
    @ApiResponse(responseCode = "200", description = "Thêm địa chỉ thành công",
            content = @Content(schema = @Schema(implementation = Address.class)))
    @PostMapping
    public ResponseEntity<BaseResponse<Address>> addAddress(@RequestBody Address address) {
        String street = "Đường " + (address.getStreet() != null ? address.getStreet() : "");
        String fullAddress = String.format("%s, %s, %s, Việt Nam",
                street,
                address.getDistrict() != null ? address.getDistrict() : "",
                address.getProvince() != null ? address.getProvince() : ""
        );

        // Gọi geocoding để lấy toạ độ
        Optional<Coordinates> coords = geocodingService.forwardGeocode(fullAddress.trim());
        coords.ifPresent(coordinates -> {
            address.setLatitude(coordinates.getLatitude());
            address.setLongitude(coordinates.getLongitude());
        });

        // Lưu vào DB
        addressService.saveAddress(address);
        return ResponseEntity.ok(BaseResponse.ok(address));
    }


    @Operation(
            summary = "Lấy danh sách tất cả địa chỉ",
            description = "Trả về danh sách tất cả các địa chỉ hiện có trong hệ thống."
    )
    @ApiResponse(responseCode = "200", description = "Thành công",
            content = @Content(schema = @Schema(implementation = Address.class)))
    @GetMapping
    public ResponseEntity<BaseResponse<List<Address>>> getAllAddresses() {
        return ResponseEntity.ok(BaseResponse.ok(addressService.findAllAddress()));
    }

    @Operation(
            summary = "Lấy địa chỉ theo ID",
            description = "Trả về thông tin chi tiết của một địa chỉ dựa trên ID."
    )
    @ApiResponse(responseCode = "200", description = "Tìm thấy địa chỉ",
            content = @Content(schema = @Schema(implementation = Address.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Address>> getAddressById(@PathVariable Long id) {
        Optional<Address> address = addressService.findById(id);
        return address.map(value -> ResponseEntity.ok(BaseResponse.ok(value)))
                .orElseGet(() -> ResponseEntity.status(404).body(BaseResponse.error("Address not found")));
    }

    @Operation(
            summary = "Tìm kiếm địa chỉ",
            description = "Tìm kiếm các địa chỉ theo tên đường, quận/huyện hoặc tỉnh/thành phố. Có thể kết hợp nhiều tiêu chí."
    )
    @ApiResponse(responseCode = "200", description = "Danh sách địa chỉ phù hợp",
            content = @Content(schema = @Schema(implementation = AddressDTO.class)))
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<AddressDTO>>> searchAddresses(
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String province) {
        return ResponseEntity.ok(BaseResponse.ok(addressService.findByDynamicFilter(street, district, province)));
    }

    @Operation(
            summary = "Cập nhật địa chỉ",
            description = "Cập nhật thông tin một địa chỉ cụ thể bằng ID. Nếu không tìm thấy địa chỉ, trả về lỗi 404."
    )
    @ApiResponse(responseCode = "200", description = "Cập nhật thành công",
            content = @Content(schema = @Schema(implementation = Address.class)))
    @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Address>> updateAddress(@PathVariable Long id, @RequestBody Address newAddress) {
        try {
            Address updatedAddress = addressService.updateAddress(id, newAddress);
            return ResponseEntity.ok(BaseResponse.ok(updatedAddress));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error("Address not found"));
        }
    }

    @Operation(
            summary = "Xóa địa chỉ",
            description = "Xóa một địa chỉ khỏi hệ thống dựa trên ID."
    )
    @ApiResponse(responseCode = "200", description = "Xóa thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy địa chỉ")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.ok(BaseResponse.ok(null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error("Address not found"));
        }
    }

    @Operation(
            summary = "Lấy nhiều địa chỉ theo danh sách ID",
            description = "Truy xuất danh sách các địa chỉ dựa trên danh sách ID được cung cấp."
    )
    @ApiResponse(responseCode = "200", description = "Truy vấn thành công",
            content = @Content(schema = @Schema(implementation = AddressDTO.class)))
    @PostMapping("/batch")
    public ResponseEntity<BaseResponse<List<AddressDTO>>> getAddressesByIds(@RequestBody List<Long> ids) {
        List<AddressDTO> result = addressService.findByIds(ids);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @PostMapping("/batch/summary")
    public ResponseEntity<BaseResponse<List<AddressSummaryDTO>>> getAddressSummary(@RequestBody List<Long> ids) {
        List<AddressSummaryDTO> result = addressService.getAddressSummary(ids);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/test-retry")
    public ResponseEntity<BaseResponse<String>> testRetry() {
        int callCount = callCounter.incrementAndGet();
        if (callCount < 3) {
            // Simulate temporary error for first two calls
            log.info("Retry attempt: {}", callCount);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new BaseResponse<>(false, "Service temporarily unavailable", null));
        } else {
            // Reset counter after successful call
            callCounter.set(0);
            return ResponseEntity.ok(new BaseResponse<>(true, "Success", "Data from address-service"));
        }
    }


}
