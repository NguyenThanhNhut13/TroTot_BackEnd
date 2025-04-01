package vn.edu.iuh.fit.addressservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.addressservice.entity.District;
import vn.edu.iuh.fit.addressservice.repository.DistrictRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictService {
    private final DistrictRepository districtRepository;

    public List<District> getAllDistricts() {
        return districtRepository.findAll();
    }

    public List<District> getDistrictsByProvinceCode(String provinceCode) {
        return districtRepository.findByProvince_Code(provinceCode);
    }
}
