/*
 * @ (#) AddressIntegrationService.java       1.0     15/05/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 15/05/2025
 * @version:    1.0
 */

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.roomservice.client.AddressClient;
import vn.edu.iuh.fit.roomservice.exception.TooManyRequestsException;
import vn.edu.iuh.fit.roomservice.model.dto.AddressDTO;
import vn.edu.iuh.fit.roomservice.model.dto.AddressSummaryDTO;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AddressIntegrationService {

    private final AddressClient addressClient;

    @RateLimiter(name = "addressRateLimiter", fallbackMethod = "fallbackGetAddressSummary")
    @Retry(name = "addressRetry", fallbackMethod = "fallbackGetAddressSummary")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "fallbackGetAddressSummary")
    public ResponseEntity<BaseResponse<List<AddressSummaryDTO>>> getAddressSummary(List<Long> ids) {
        return addressClient.getAddressSummary(ids);
    }

    @RateLimiter(name = "addressRateLimiter", fallbackMethod = "fallbackGetAddressById")
    @Retry(name = "addressRetry", fallbackMethod = "fallbackGetAddressById")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "fallbackGetAddressById")
    public ResponseEntity<BaseResponse<AddressDTO>> getAddressById(Long id) {
        return addressClient.getAddressById(id);
    }

    // Thêm method addAddress
    @RateLimiter(name = "addressRateLimiter", fallbackMethod = "fallbackAddAddress")
    @Retry(name = "addressRetry", fallbackMethod = "fallbackAddAddress")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "fallbackAddAddress")
    public ResponseEntity<BaseResponse<AddressSummaryDTO>> addAddress(AddressDTO addressDTO) {
        return addressClient.addAddress(addressDTO);
    }

    // Thêm method updateAddress
    @RateLimiter(name = "addressRateLimiter", fallbackMethod = "fallbackUpdateAddress")
    @Retry(name = "addressRetry", fallbackMethod = "fallbackUpdateAddress")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "fallbackUpdateAddress")
    public ResponseEntity<BaseResponse<AddressSummaryDTO>> updateAddress(Long id, AddressDTO addressDTO) {
        return addressClient.updateAddress(id, addressDTO);
    }

    public ResponseEntity<BaseResponse<List<AddressSummaryDTO>>> fallbackGetAddressSummary(List<Long> ids, Throwable t) {
        if (t instanceof RequestNotPermitted) {
            throw new TooManyRequestsException("You are sending too many requests, please try again later.");
        } else if (t instanceof CallNotPermittedException) {
            BaseResponse<List<AddressSummaryDTO>> response = new BaseResponse<>(
                    false,
                    "Circuit breaker open. Please try again later.",
                    Collections.emptyList()
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        } else {
            BaseResponse<List<AddressSummaryDTO>> response = new BaseResponse<>(
                    false,
                    "Service temporarily unavailable: " + t.getMessage(),
                    Collections.emptyList()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Fallback for getAddressById
    public ResponseEntity<BaseResponse<AddressSummaryDTO>> fallbackGetAddressById(Long id, Throwable t) {
        if (t instanceof RequestNotPermitted) {
            throw new TooManyRequestsException("Too many requests, please try again later.");
        } else if (t instanceof CallNotPermittedException) {
            BaseResponse<AddressSummaryDTO> response = new BaseResponse<>(
                    false,
                    "Circuit breaker open. Please try again later.",
                    null
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        } else {
            BaseResponse<AddressSummaryDTO> response = new BaseResponse<>(
                    false,
                    "Service temporarily unavailable: " + t.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




}
