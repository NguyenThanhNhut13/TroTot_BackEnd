/*
 * @ (#) UserClient.java       1.0     09/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.client;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 09/03/2025
 * @version:    1.0
 */

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.authservice.model.dto.request.RegisterProfileRequest;
import vn.edu.iuh.fit.authservice.model.dto.request.RegisterRequest;

@FeignClient(name = "user-service")
public interface UserClient {

    @PostMapping("/api/v1/users/create")
    ResponseEntity<?> createUserInfo(@RequestBody RegisterProfileRequest request);

}
