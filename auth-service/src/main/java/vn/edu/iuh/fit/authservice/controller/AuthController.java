/*
 * @ (#) AuthController.java       1.0     09/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.controller;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 09/03/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.iuh.fit.authservice.entity.request.LoginRequest;
import vn.edu.iuh.fit.authservice.entity.request.RegisterRequest;
import vn.edu.iuh.fit.authservice.entity.request.VerifyOtpRequest;
import vn.edu.iuh.fit.authservice.entity.response.LoginResponse;
import vn.edu.iuh.fit.authservice.exception.ErrorResponse;
import vn.edu.iuh.fit.authservice.exception.InvalidCredentialsException;
import vn.edu.iuh.fit.authservice.exception.UserNotFoundException;
import vn.edu.iuh.fit.authservice.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = authService.login(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("INVALID_CREDENTIALS", e.getMessage()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", e.getMessage()));
        }

//        catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ErrorResponse("SERVER_ERROR", "An unexpected error occurred"));
//        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("Login successful! Please check your email or phone to receive OTP");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok("Authentication successful!");
    }


}
