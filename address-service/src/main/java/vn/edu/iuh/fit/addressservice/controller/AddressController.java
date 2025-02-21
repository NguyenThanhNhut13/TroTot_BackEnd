/*
 * @ (#) UserController.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.addressservice.controller;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 11/02/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.addressservice.dto.AddressDTO;
import vn.edu.iuh.fit.addressservice.entity.Address;
import vn.edu.iuh.fit.addressservice.service.AddressService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody Address address) {
        addressService.saveAddress(address);
    }

    @GetMapping
    public ResponseEntity<List<Address>> findAllAddress() {
        return ResponseEntity.ok(addressService.findAllAddress());
    }

    @GetMapping("/search")
    public ResponseEntity<List<AddressDTO>> findByDynamicFilter(@RequestParam(required = false) String street,
                                                           @RequestParam(required = false) String district,
                                                           @RequestParam(required = false) String city) {
        return ResponseEntity.ok(addressService.findByDynamicFilter(street, district, city));
    }

}
