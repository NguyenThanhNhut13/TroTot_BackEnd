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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.authservice.dto.UserAuthDTO;
import vn.edu.iuh.fit.authservice.dto.UserDTO;
import vn.edu.iuh.fit.authservice.entity.request.RegisterRequest;

@FeignClient(name = "user-service", url = "${application.config.users-url}")
public interface UserClient {

    @GetMapping("/auth-info")
    ResponseEntity<UserAuthDTO> getAuthInfo(@RequestParam String credential);

    @GetMapping("/info")
    ResponseEntity<UserDTO> getUserInfo(@RequestParam String credential);

    @PostMapping("/create")
    ResponseEntity<?> createUser(@RequestBody RegisterRequest request);
}
