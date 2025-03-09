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
import vn.edu.iuh.fit.userservice.dto.UserDTO;
import vn.edu.iuh.fit.userservice.dto.request.LoginRequest;
import vn.edu.iuh.fit.userservice.entity.User;
import vn.edu.iuh.fit.userservice.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody User user) {
        userService.saveUser(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> findAllUser() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserDTO> validateUser(@RequestBody LoginRequest request) {
        boolean isAuthenticated = userService.authenticate(request.getCredential(), request.getPassword());
        System.out.println(request.getPassword());

        if (isAuthenticated) {
            UserDTO userDto = userService.findByEmailOrPhone(request.getCredential());
            return ResponseEntity.ok(userDto);
        }

        return ResponseEntity.ok(null);
    }

}
