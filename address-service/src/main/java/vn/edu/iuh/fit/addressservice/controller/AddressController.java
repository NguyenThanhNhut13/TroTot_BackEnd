package vn.edu.iuh.fit.addressservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.dto.AddressDTO;
import vn.edu.iuh.fit.addressservice.entity.Address;
import vn.edu.iuh.fit.addressservice.service.AddressService;
import vn.edu.iuh.fit.addressservice.util.ApiResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    // Thêm địa chỉ mới
    @PostMapping
    public ResponseEntity<ApiResponse<Address>> addAddress(@RequestBody Address address) {
        addressService.saveAddress(address);
        return ResponseEntity.ok(ApiResponse.success(address));
    }

    // Lấy tất cả địa chỉ
    @GetMapping
    public ResponseEntity<ApiResponse<List<Address>>> getAllAddresses() {
        return ResponseEntity.ok(ApiResponse.success(addressService.findAllAddress()));
    }

    // Tìm kiếm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Address>> getAddressById(@PathVariable Long id) {
        Optional<Address> address = addressService.findById(id);
        return address.map(value -> ResponseEntity.ok(ApiResponse.success(value)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(ApiResponse.error(404, "Address not found")));
    }

    // Tìm kiếm địa chỉ theo bộ lọc
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<AddressDTO>>> searchAddresses(
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String province) {
        return ResponseEntity.ok(ApiResponse.success(addressService.findByDynamicFilter(street, district, province)));
    }

    // Cập nhật địa chỉ
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Address>> updateAddress(@PathVariable Long id, @RequestBody Address newAddress) {
        try {
            Address updatedAddress = addressService.updateAddress(id, newAddress);
            return ResponseEntity.ok(ApiResponse.success(updatedAddress));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(ApiResponse.error(404, "Address not found"));
        }
    }

    // Xóa địa chỉ
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
