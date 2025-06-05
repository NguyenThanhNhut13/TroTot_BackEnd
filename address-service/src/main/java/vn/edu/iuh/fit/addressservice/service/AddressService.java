/*
 * @ (#) AddressService.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.addressservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.addressservice.dto.AddressDTO;
import vn.edu.iuh.fit.addressservice.dto.AddressSummaryDTO;
import vn.edu.iuh.fit.addressservice.entity.Address;
import vn.edu.iuh.fit.addressservice.repository.AddressRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public void saveAddress(Address address) {
        addressRepository.save(address);
    }

    public List<Address> findAllAddress() {
        return addressRepository.findAll();
    }

    public List<AddressDTO> findByDynamicFilter(String street, String district, String city) {
        List<Address> addresses = addressRepository.findByProvinceLikeAndDistrictLikeAndStreetLike(street, district, city);

        return addresses.stream()
                .map(address -> AddressDTO.builder()
                        .id(address.getId())
                        .province(address.getProvince())
                        .district(address.getDistrict())
                        .ward(address.getWard())
                        .street(address.getStreet())
                        .houseNumber(address.getHouseNumber())
                        .longitude(address.getLongitude())
                        .latitude(address.getLatitude())
                        .build()
                ).toList();
    }

    public Optional<Address> findById(Long id) {
        return addressRepository.findById(id);
    }

    public Address updateAddress(Long id, Address newAddress) {
        return addressRepository.findById(id)
                .map(existingAddress -> {
                    existingAddress.setProvince(newAddress.getProvince());
                    existingAddress.setDistrict(newAddress.getDistrict());
                    existingAddress.setWard(newAddress.getWard());
                    existingAddress.setStreet(newAddress.getStreet());
                    existingAddress.setHouseNumber(newAddress.getHouseNumber());
                    existingAddress.setLatitude(newAddress.getLatitude());
                    existingAddress.setLongitude(newAddress.getLongitude());
                    return addressRepository.save(existingAddress);
                }).orElseThrow(() -> new RuntimeException("Address not found with id: " + id));
    }

    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found with id: " + id);
        }
        addressRepository.deleteById(id);
    }

    public List<AddressDTO> findByIds(List<Long> ids) {
        List<Address> addresses = addressRepository.findProjectionsByIds(ids);
        return addresses.stream()
                .map(address -> AddressDTO.builder()
                        .id(address.getId())
                        .province(address.getProvince())
                        .district(address.getDistrict())
                        .ward(address.getWard())
                        .street(address.getStreet())
                        .houseNumber(address.getHouseNumber())
                        .longitude(address.getLongitude())
                        .latitude(address.getLatitude())
                        .build()
                ).toList();
    }


    public List<AddressSummaryDTO> getAddressSummary(List<Long> ids) {
        return addressRepository.findAddressSummaryByIds(ids);
    }

}
