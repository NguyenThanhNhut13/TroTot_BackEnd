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
import vn.edu.iuh.fit.addressservice.entity.Address;
import vn.edu.iuh.fit.addressservice.repository.AddressRepository;

import java.util.List;

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
}
