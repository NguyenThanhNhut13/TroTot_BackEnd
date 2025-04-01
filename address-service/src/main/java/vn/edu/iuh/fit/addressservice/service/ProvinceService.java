package vn.edu.iuh.fit.addressservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.addressservice.entity.Province;
import vn.edu.iuh.fit.addressservice.repository.ProvinceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinceService {
    private final ProvinceRepository provinceRepository;

    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }
}
