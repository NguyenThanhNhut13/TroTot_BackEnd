package vn.edu.iuh.fit.addressservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.entity.District;
import vn.edu.iuh.fit.addressservice.entity.Province;
import vn.edu.iuh.fit.addressservice.entity.Ward;
import vn.edu.iuh.fit.addressservice.repository.DistrictRepository;
import vn.edu.iuh.fit.addressservice.repository.ProvinceRepository;
import vn.edu.iuh.fit.addressservice.repository.WardRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    @GetMapping("/provinces/getAll")
    public ResponseEntity<List<Province>> getAllProvinces() {
        List<Province> provinces = provinceRepository.findAll();
        return ResponseEntity.ok(provinces);
    }

    @GetMapping("/districts/getAll")
    public ResponseEntity<List<District>> getAllDistricts() {
        List<District> districts = districtRepository.findAll();
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/districts/getByProvince")
    public ResponseEntity<List<District>> getDistrictsByProvince(@RequestParam String provinceCode) {
        List<District> districts = districtRepository.findByProvince_Code(provinceCode);
        return ResponseEntity.ok(districts);
    }

    @GetMapping("/wards/getAll")
    public ResponseEntity<List<Ward>> getAllWards() {
        List<Ward> wards = wardRepository.findAll();
        return ResponseEntity.ok(wards);
    }

    @GetMapping("/wards/getByDistrict")
    public ResponseEntity<List<Ward>> getWardsByDistrict(@RequestParam String districtCode) {
        List<Ward> wards = wardRepository.findByDistrict_Code(districtCode);
        return ResponseEntity.ok(wards);
    }
}
