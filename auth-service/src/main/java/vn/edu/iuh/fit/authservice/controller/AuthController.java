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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.authservice.model.dto.request.LoginRequest;
import vn.edu.iuh.fit.authservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.authservice.model.dto.request.ResetPasswordRequest;
import vn.edu.iuh.fit.authservice.model.dto.request.VerifyOtpRequest;
import vn.edu.iuh.fit.authservice.model.dto.response.AccountInfoResponse;
import vn.edu.iuh.fit.authservice.model.dto.response.BaseResponse;
import vn.edu.iuh.fit.authservice.model.dto.response.LoginResponse;
import vn.edu.iuh.fit.authservice.model.dto.response.TokenResponse;
import vn.edu.iuh.fit.authservice.service.AuthService;
import vn.edu.iuh.fit.authservice.service.JwtService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(new BaseResponse<>(true, "Login successful", loginResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Register successful! Please check your email or phone to receive OTP", null)
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Authentication successful!", null)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String accessToken = request.get("accessToken");
        TokenResponse tokenResponse = authService.refreshAccessToken(accessToken, refreshToken);
        return ResponseEntity.ok(new BaseResponse<>(true, "Refresh token successful", tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader,
                                    @RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String accessToken = authHeader.replace("Bearer ", "");
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Logout successful!", null)
        );
    }

    @PostMapping("/forgot-password/request/{credential}")
    public ResponseEntity<?> forgotPasswordRequest(@PathVariable("credential") String credential) {
        authService.forgotPasswordRequest(credential);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Create request successful! Please check your email or phone to receive OTP", null)
        );
    }

    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> forgotPasswordVerifyOtp(@RequestBody VerifyOtpRequest request) {
        String token = authService.verifyOtpAndGenerateToken(request);
        return ResponseEntity.ok(new BaseResponse<>(true, "OTP verified.", token));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new BaseResponse<>(true, "Password reset successful.", null));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAccountInfo(@RequestHeader("Authorization") String authHeader) {
        AccountInfoResponse accountInfoResponse = authService.getAccountInfo(authHeader);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Account info successful.", accountInfoResponse)
        );
    }



}
