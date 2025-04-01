package vn.edu.iuh.fit.addressservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.dto.LocationDTO;
import vn.edu.iuh.fit.addressservice.entity.District;
import vn.edu.iuh.fit.addressservice.entity.Province;
import vn.edu.iuh.fit.addressservice.entity.Ward;
import vn.edu.iuh.fit.addressservice.service.*;
import vn.edu.iuh.fit.addressservice.util.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final ProvinceService provinceService;
    private final DistrictService districtService;
    private final WardService wardService;
    private final AddressService addressService;

    @GetMapping("/provinces/getAll")
    public ResponseEntity<List<Province>> getAllProvinces() {
        return ResponseEntity.ok(provinceService.getAllProvinces());
    }

    @GetMapping("/districts/getAll")
    public ResponseEntity<List<District>> getAllDistricts() {
        return ResponseEntity.ok(districtService.getAllDistricts());
    }

    @GetMapping("/districts/getByProvince")
    public ResponseEntity<List<District>> getDistrictsByProvince(@RequestParam String provinceCode) {
        return ResponseEntity.ok(districtService.getDistrictsByProvinceCode(provinceCode));
    }

    @GetMapping("/wards/getAll")
    public ResponseEntity<List<Ward>> getAllWards() {
        return ResponseEntity.ok(wardService.getAllWards());
    }

    @GetMapping("/wards/getByDistrict")
    public ResponseEntity<List<Ward>> getWardsByDistrict(@RequestParam String districtCode) {
        return ResponseEntity.ok(wardService.getWardsByDistrictCode(districtCode));
    }

    @Operation(summary = "Tìm kiếm địa chỉ", description = "Tìm kiếm theo tên tỉnh, quận, phường")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<LocationDTO>>> searchAddresses(
            @RequestParam(required = false) String province,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String ward) {

        List<LocationDTO> result = addressService.searchLocations(ward, district, province);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
