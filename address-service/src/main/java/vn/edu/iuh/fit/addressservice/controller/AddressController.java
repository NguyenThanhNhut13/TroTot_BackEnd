package vn.edu.iuh.fit.addressservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.dto.AddressDTO;
import vn.edu.iuh.fit.addressservice.entity.Address;
import vn.edu.iuh.fit.addressservice.entity.Coordinates;
import vn.edu.iuh.fit.addressservice.service.AddressService;
import vn.edu.iuh.fit.addressservice.service.GeocodingService;
import vn.edu.iuh.fit.addressservice.util.BaseResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final GeocodingService geocodingService;
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


    @GetMapping
    public ResponseEntity<BaseResponse<List<Address>>> getAllAddresses() {
        return ResponseEntity.ok(BaseResponse.ok(addressService.findAllAddress()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Address>> getAddressById(@PathVariable Long id) {
        Optional<Address> address = addressService.findById(id);
        return address.map(value -> ResponseEntity.ok(BaseResponse.ok(value)))
                .orElseGet(() -> ResponseEntity.status(404).body(BaseResponse.error("Address not found")));
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<List<AddressDTO>>> searchAddresses(
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String province) {
        return ResponseEntity.ok(BaseResponse.ok(addressService.findByDynamicFilter(street, district, province)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Address>> updateAddress(@PathVariable Long id, @RequestBody Address newAddress) {
        try {
            Address updatedAddress = addressService.updateAddress(id, newAddress);
            return ResponseEntity.ok(BaseResponse.ok(updatedAddress));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error("Address not found"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.ok(BaseResponse.ok(null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(BaseResponse.error("Address not found"));
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<BaseResponse<List<AddressDTO>>> getAddressesByIds(@RequestBody List<Long> ids) {
        List<AddressDTO> result = addressService.findByIds(ids);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

}
