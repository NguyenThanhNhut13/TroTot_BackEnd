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
import vn.edu.iuh.fit.userservice.dto.UserAuthDTO;
import vn.edu.iuh.fit.userservice.dto.UserDTO;
import vn.edu.iuh.fit.userservice.entity.User;
import vn.edu.iuh.fit.userservice.entity.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<User>> findAllUser() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/auth-info")
    public ResponseEntity<UserAuthDTO> getAuthInfo(@RequestParam String credential) {
        UserAuthDTO user = userService.getAuthInfoByCredential(credential);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(user);
    }

    @GetMapping("/info")
    public ResponseEntity<UserDTO> getUserInfo(@RequestParam String credential) {
        UserDTO userDTO = userService.getUserByCredential(credential);
        return ResponseEntity.ok(userDTO);
    }

}
