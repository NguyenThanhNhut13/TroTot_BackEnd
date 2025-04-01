/*
 * @ (#) UserController.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.controller;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.userservice.model.dto.reponse.BaseResponse;
import vn.edu.iuh.fit.userservice.model.dto.reponse.UserProfileResponse;
import vn.edu.iuh.fit.userservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.service.UserProfileService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUserInfo(@RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Add user profile success!", null)
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo() {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = Long.parseLong(principal);

        UserProfileResponse userDTO = userService.getUserProfile(userId);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "User profile retrieved successfully!", userDTO)
        );
    }

}
