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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AddressIntegrationService.class);

    private final AddressClient addressClient;

    // getAddressSummary - Rate Limiter before Circuit Breaker
    @Retry(name = "addressServiceRetry", fallbackMethod = "fallbackGetAddressSummary")
    @CircuitBreaker(name = "addressService", fallbackMethod = "fallbackGetAddressSummary")
    @RateLimiter(name = "addressServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<BaseResponse<List<AddressSummaryDTO>>> getAddressSummary(List<Long> ids) {
        return addressClient.getAddressSummary(ids);
    }

    // getAddressById - Rate Limiter before Circuit Breaker
    @Retry(name = "addressServiceRetry", fallbackMethod = "fallbackGetAddressById")
    @CircuitBreaker(name = "addressService", fallbackMethod = "fallbackGetAddressById")
    @RateLimiter(name = "addressServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<BaseResponse<AddressDTO>> getAddressById(Long id) {
        return addressClient.getAddressById(id);
    }

    // addAddress - Rate Limiter before Circuit Breaker
    @CircuitBreaker(name = "addressService", fallbackMethod = "fallbackAddAddress")
    @RateLimiter(name = "addressServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<BaseResponse<AddressDTO>> addAddress(AddressDTO addressDTO) {
        return addressClient.addAddress(addressDTO);
    }

    // updateAddress - Rate Limiter before Circuit Breaker
    @CircuitBreaker(name = "addressService", fallbackMethod = "fallbackUpdateAddress")
    @RateLimiter(name = "addressServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<BaseResponse<AddressDTO>> updateAddress(Long id, AddressDTO addressDTO) {
        return addressClient.updateAddress(id, addressDTO);
    }

    // searchAddresses - Rate Limiter before Circuit Breaker
    @Retry(name = "addressServiceRetry", fallbackMethod = "fallbackSearchAddresses")
    @CircuitBreaker(name = "addressService", fallbackMethod = "fallbackSearchAddresses")
    @RateLimiter(name = "addressServiceRateLimiter", fallbackMethod = "rateLimiterFallback")
    public ResponseEntity<BaseResponse<List<AddressDTO>>> searchAddresses(String street, String district, String city) {
        return addressClient.searchAddresses(street, district, city);
    }

    // Generic Rate Limiter fallback for all methods
    public <T> ResponseEntity<BaseResponse<T>> rateLimiterFallback(Throwable t) throws TooManyRequestsException {
        throw new TooManyRequestsException("Too many requests. Please try again later.");
    }

    // Fallback method for getAddressSummary
    public ResponseEntity<BaseResponse<List<AddressSummaryDTO>>> fallbackGetAddressSummary(List<Long> ids, Throwable t) {
        return handleServiceFallback(t, Collections.emptyList());
    }

    // Fallback method for getAddressById
    public ResponseEntity<BaseResponse<AddressDTO>> fallbackGetAddressById(Long id, Throwable t) {
        return handleServiceFallback(t, null);
    }

    // Fallback method for addAddress
    public ResponseEntity<BaseResponse<AddressDTO>> fallbackAddAddress(AddressDTO addressDTO, Throwable t) {
        return handleServiceFallback(t, null);
    }

    // Fallback method for updateAddress
    public ResponseEntity<BaseResponse<AddressDTO>> fallbackUpdateAddress(Long id, AddressDTO addressDTO, Throwable t) {
        return handleServiceFallback(t, null);
    }

    // Fallback method for searchAddresses
    public ResponseEntity<BaseResponse<List<AddressDTO>>> fallbackSearchAddresses(String street, String district, String city, Throwable t) {
        return handleServiceFallback(t, Collections.emptyList());
    }

    // Generic service fallback handler (for Circuit Breaker and common faults)
    private <T> ResponseEntity<BaseResponse<T>> handleServiceFallback(Throwable t, T defaultValue) {
        if (t instanceof CallNotPermittedException) {
            log.warn("Circuit breaker is open: {}", t.getMessage());
            BaseResponse<T> response = new BaseResponse<>(
                    false,
                    "Service is currently unavailable. Please try again later.",
                    defaultValue
            );
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        } else if (t instanceof TooManyRequestsException) {
            throw (TooManyRequestsException) t;
        } else {
            log.error("Service error: {}", t.getMessage());
            BaseResponse<T> response = new BaseResponse<>(
                    false,
                    "An unexpected error occurred: " + t.getMessage(),
                    defaultValue
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper method to extract throwable from parameters
    private Throwable extractThrowable(Object[] args) {
        if (args != null && args.length > 0) {
            Object lastArg = args[args.length - 1];
            if (lastArg instanceof Throwable) {
                return (Throwable) lastArg;
            }
        }
        return new Exception("Unknown rate limiting error");
    }
}
