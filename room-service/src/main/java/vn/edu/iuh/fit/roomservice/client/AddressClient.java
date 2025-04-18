/*
 * @ (#) AddressClient.java       1.0     21/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 21/02/2025
 * @version:    1.0
 */

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.roomservice.model.dto.AddressDTO;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;

import java.util.List;

@FeignClient(name = "address-service")
public interface AddressClient {

    @GetMapping("/api/v1/addresses/search")
    ResponseEntity<BaseResponse<List<AddressDTO>>> searchAddresses(
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String province);

    @PostMapping("/api/v1/addresses")
    ResponseEntity<BaseResponse<AddressDTO>> addAddress(@RequestBody AddressDTO address);

    @GetMapping("/api/v1/addresses/{id}")
    ResponseEntity<BaseResponse<AddressDTO>> getAddressById(@PathVariable Long id);

    @PutMapping("/api/v1/addresses/{id}")
    ResponseEntity<BaseResponse<AddressDTO>> updateAddress(@PathVariable Long id, @RequestBody AddressDTO newAddress);
}
