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
import vn.edu.iuh.fit.roomservice.model.dto.AddressSummaryDTO;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;

import java.util.List;

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





}
