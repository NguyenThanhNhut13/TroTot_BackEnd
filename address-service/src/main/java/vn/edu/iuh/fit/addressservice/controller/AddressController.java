package vn.edu.iuh.fit.addressservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.dto.AddressDTO;
import vn.edu.iuh.fit.addressservice.entity.Address;
import vn.edu.iuh.fit.addressservice.service.AddressService;
import vn.edu.iuh.fit.addressservice.util.ApiResponse;

import java.util.List;
import java.util.Optional;

@Tag(name = "Address Management", description = "API cho quản lý địa chỉ")
@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "Thêm địa chỉ mới", description = "Thêm một địa chỉ mới vào hệ thống")
    @PostMapping
    public ResponseEntity<ApiResponse<Address>> addAddress(@RequestBody Address address) {
        addressService.saveAddress(address);
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    @Operation(summary = "Lấy tất cả địa chỉ", description = "Trả về danh sách tất cả địa chỉ có trong hệ thống")
    @GetMapping
    public ResponseEntity<ApiResponse<List<Address>>> getAllAddresses() {
        return ResponseEntity.ok(ApiResponse.success(addressService.findAllAddress()));
    }

    @Operation(summary = "Tìm kiếm địa chỉ theo ID", description = "Tìm kiếm địa chỉ dựa trên ID đã cung cấp")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Address>> getAddressById(@PathVariable Long id) {
        Optional<Address> address = addressService.findById(id);
        return address.map(value -> ResponseEntity.ok(ApiResponse.success(value)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error(404, "Address not found")));
    }

    @Operation(summary = "Tìm kiếm địa chỉ theo bộ lọc", description = "Lọc địa chỉ theo các tiêu chí: đường, quận/huyện, tỉnh/thành phố")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AddressDTO>>> searchAddresses(
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String province) {
        return ResponseEntity.ok(ApiResponse.success(addressService.findByDynamicFilter(street, district, province)));
    }

    @Operation(summary = "Cập nhật địa chỉ", description = "Cập nhật thông tin của một địa chỉ dựa trên ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Address>> updateAddress(@PathVariable Long id, @RequestBody Address newAddress) {
        try {
            Address updatedAddress = addressService.updateAddress(id, newAddress);
            return ResponseEntity.ok(ApiResponse.success(updatedAddress));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(404, "Address not found"));
        }
    }

    @Operation(summary = "Xóa địa chỉ", description = "Xóa địa chỉ dựa trên ID đã cung cấp")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.ok(ApiResponse.success(null)); // data = null khi không có dữ liệu trả về
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(404, "Address not found"));
        }
    }
}
