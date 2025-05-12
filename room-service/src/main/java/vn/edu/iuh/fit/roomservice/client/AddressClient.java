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

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.roomservice.model.dto.AddressDTO;
import vn.edu.iuh.fit.roomservice.model.dto.AddressSummaryDTO;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;

import java.util.Collections;
import java.util.List;

@FeignClient(name = "address-service")
public interface AddressClient {

    Logger log = LoggerFactory.getLogger(AddressClient.class);

    @GetMapping("/api/v1/addresses/search")
    @CircuitBreaker(name = "addressService", fallbackMethod = "searchAddressFallback")
    ResponseEntity<BaseResponse<List<AddressDTO>>> searchAddresses(
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String province);

    @PostMapping("/api/v1/addresses")
    @CircuitBreaker(name = "addressService", fallbackMethod = "addAddressFallback")
    ResponseEntity<BaseResponse<AddressDTO>> addAddress(@RequestBody AddressDTO address);

    @GetMapping("/api/v1/addresses/{id}")
    ResponseEntity<BaseResponse<AddressDTO>> getAddressById(@PathVariable Long id);

    @PutMapping("/api/v1/addresses/{id}")
    ResponseEntity<BaseResponse<AddressDTO>> updateAddress(@PathVariable Long id, @RequestBody AddressDTO newAddress);

    @PostMapping("/api/v1/addresses/batch")
    ResponseEntity<BaseResponse<List<AddressDTO>>> getAddressesByIds(@RequestBody List<Long> ids);

    @PostMapping("/api/v1/addresses/batch/summary")
    ResponseEntity<BaseResponse<List<AddressSummaryDTO>>> getAddressSummary(@RequestBody List<Long> ids);

    default ResponseEntity<BaseResponse<List<AddressDTO>>> searchAddressFallback(
            String street, String district, String province, Throwable t) {

        log.error("Fallback triggered for searchAddresses: {}", t.getMessage());

        BaseResponse<List<AddressDTO>> fallbackResponse = new BaseResponse<>(
                false,
                "Service is temporarily unavailable. This is a fallback response.",
                Collections.emptyList()
        );

        return ResponseEntity.ok(fallbackResponse);
    }

    default ResponseEntity<BaseResponse<AddressDTO>> addAddressFallback(
            AddressDTO address, Throwable t) {

        log.error("Fallback triggered for addAddress: {}", t.getMessage());

        BaseResponse<AddressDTO> fallbackResponse = new BaseResponse<>(
                false,
                "Service is temporarily unavailable. Unable to add address.",
                null
        );

        return ResponseEntity.ok(fallbackResponse);
    }

}
