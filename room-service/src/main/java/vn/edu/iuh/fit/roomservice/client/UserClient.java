/*
 * @ (#) UserClient.java       1.0     20/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 20/04/2025
 * @version:    1.0
 */

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import vn.edu.iuh.fit.roomservice.config.FeignClientConfig;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;

@FeignClient(name = "user-service", configuration = FeignClientConfig.class)
public interface UserClient {

    Logger log = LoggerFactory.getLogger(UserClient.class);

    @PostMapping("/api/v1/users/use-post-slot")
    @CircuitBreaker(name = "userService", fallbackMethod = "usePostSlotFallback")
    ResponseEntity<BaseResponse<Integer>> usePostSlot();

}
