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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.userservice.model.dto.reponse.BaseResponse;
import vn.edu.iuh.fit.userservice.model.dto.reponse.UserProfileResponse;
import vn.edu.iuh.fit.userservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUserInfo(@RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Add user profile success!", null)
        );
    }

//    @GetMapping("/info")
//    public ResponseEntity<UserProfileResponse> getUserInfo(@RequestParam String credential) {
//        UserProfileResponse userDTO = userService.getUserByCredential(credential);
//        return ResponseEntity.ok(userDTO);
//    }

}
