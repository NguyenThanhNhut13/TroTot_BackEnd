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

    // getAddressSummary
    @RateLimiter(name = "addressRateLimiter", fallbackMethod = "fallbackGetAddressSummary")
    @Retry(name = "addressRetry", fallbackMethod = "fallbackGetAddressSummary")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "fallbackGetAddressSummary")
    public ResponseEntity<BaseResponse<List<AddressSummaryDTO>>> getAddressSummary(List<Long> ids) {
        return addressClient.getAddressSummary(ids);
    }

    // getAddressById
    @RateLimiter(name = "addressRateLimiter", fallbackMethod = "fallbackGetAddressById")
    @Retry(name = "addressRetry", fallbackMethod = "fallbackGetAddressById")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "fallbackGetAddressById")
    public ResponseEntity<BaseResponse<AddressDTO>> getAddressById(Long id) {
        return addressClient.getAddressById(id);
    }

    // addAddress
    @RateLimiter(name = "addressRateLimiter", fallbackMethod = "fallbackAddAddress")
    @Retry(name = "addressRetry", fallbackMethod = "fallbackAddAddress")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "fallbackAddAddress")
    public ResponseEntity<BaseResponse<AddressDTO>> addAddress(AddressDTO addressDTO) {
        return addressClient.addAddress(addressDTO);
    }

    // updateAddress
    @RateLimiter(name = "addressRateLimiter", fallbackMethod = "fallbackUpdateAddress")
    @Retry(name = "addressRetry", fallbackMethod = "fallbackUpdateAddress")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "fallbackUpdateAddress")
    public ResponseEntity<BaseResponse<AddressDTO>> updateAddress(Long id, AddressDTO addressDTO) {
        return addressClient.updateAddress(id, addressDTO);
    }

    @RateLimiter(name = "addressRateLimiter", fallbackMethod = "fallbackSearchAddresses")
    @Retry(name = "addressRetry", fallbackMethod = "fallbackSearchAddresses")
    @CircuitBreaker(name = "addressCircuitBreaker", fallbackMethod = "fallbackSearchAddresses")
    public ResponseEntity<BaseResponse<List<AddressDTO>>> searchAddresses(String street, String district, String city) {
        return addressClient.searchAddresses(street, district, city);
    }

    // Fallback method for getAddressSummary
    public ResponseEntity<BaseResponse<List<AddressSummaryDTO>>> fallbackGetAddressSummary(List<Long> ids, Throwable t) {
        return handleFallback(t, Collections.emptyList());
    }

    // Fallback method for getAddressById
    public ResponseEntity<BaseResponse<AddressDTO>> fallbackGetAddressById(Long id, Throwable t) {
        return handleFallback(t, null);
    }

    // Fallback method for addAddress
    public ResponseEntity<BaseResponse<AddressDTO>> fallbackAddAddress(AddressDTO addressDTO, Throwable t) {
        return handleFallback(t, null);
    }

    // Fallback method for updateAddress
    public ResponseEntity<BaseResponse<AddressDTO>> fallbackUpdateAddress(Long id, AddressDTO addressDTO, Throwable t) {
        return handleFallback(t, null);
    }

    // Fallback method for searchAddresses
    public ResponseEntity<BaseResponse<List<AddressDTO>>> fallbackSearchAddresses(String street, String district, String city, Throwable t) {
        return handleFallback(t, Collections.emptyList());
    }

        // Generic fallback handler to reduce code duplication
    private <T> ResponseEntity<BaseResponse<T>> handleFallback(Throwable t, T defaultValue) {
        if (t instanceof RequestNotPermitted) {
            throw new TooManyRequestsException("You are sending too many requests, please try again later.");
        } else if (t instanceof CallNotPermittedException) {
            BaseResponse<T> response = new BaseResponse<>(
                    false,
                    "Circuit breaker open. Please try again later.",
                    defaultValue
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        } else {
            BaseResponse<T> response = new BaseResponse<>(
                    false,
                    "Service temporarily unavailable: " + t.getMessage(),
                    defaultValue
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



}
