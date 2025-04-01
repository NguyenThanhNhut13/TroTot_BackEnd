package vn.edu.iuh.fit.addressservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.addressservice.dto.AddressDTO;
import vn.edu.iuh.fit.addressservice.dto.LocationDTO;
import vn.edu.iuh.fit.addressservice.entity.Address;
import vn.edu.iuh.fit.addressservice.entity.Ward;
import vn.edu.iuh.fit.addressservice.repository.AddressRepository;
import vn.edu.iuh.fit.addressservice.repository.WardRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final WardRepository wardRepository;
    private final AddressRepository addressRepository;

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

    //
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
}
