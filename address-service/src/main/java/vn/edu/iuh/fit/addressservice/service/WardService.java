package vn.edu.iuh.fit.addressservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.addressservice.entity.Ward;
import vn.edu.iuh.fit.addressservice.repository.WardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WardService {
    private final WardRepository wardRepository;

    public List<Ward> getAllWards() {
        return wardRepository.findAll();
    }

    public List<Ward> getWardsByDistrictCode(String districtCode) {
        return wardRepository.findByDistrict_Code(districtCode);
    }
}
