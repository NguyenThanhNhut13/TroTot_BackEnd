package vn.edu.iuh.fit.addressservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.addressservice.dto.LocationDTO;
import vn.edu.iuh.fit.addressservice.entity.Ward;
import vn.edu.iuh.fit.addressservice.repository.WardRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final WardRepository wardRepository;

    public List<LocationDTO> searchLocations(String wardName, String districtName, String provinceName) {
        List<Ward> allWards = wardRepository.findAll();

        return allWards.stream()
                .filter(w -> wardName == null || w.getName().toLowerCase().contains(wardName.toLowerCase()))
                .filter(w -> districtName == null || w.getDistrict().getName().toLowerCase().contains(districtName.toLowerCase()))
                .filter(w -> provinceName == null || w.getDistrict().getProvince().getName().toLowerCase().contains(provinceName.toLowerCase()))
                .map(w -> LocationDTO.builder()
                        .wardName(w.getName())
                        .wardCode(w.getCode())
                        .districtName(w.getDistrict().getName())
                        .districtCode(w.getDistrict().getCode())
                        .provinceName(w.getDistrict().getProvince().getName())
                        .provinceCode(w.getDistrict().getProvince().getCode())
                        .build()
                )
                .toList();
    }
}
