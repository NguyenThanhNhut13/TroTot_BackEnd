/*
 * @ (#) AuthService.java       1.0     13/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 13/03/2025
 * @version:    1.0
 */

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.authservice.client.UserClient;
import vn.edu.iuh.fit.authservice.dto.UserAuthDTO;
import vn.edu.iuh.fit.authservice.dto.UserDTO;
import vn.edu.iuh.fit.authservice.entity.request.LoginRequest;
import vn.edu.iuh.fit.authservice.entity.request.RegisterRequest;
import vn.edu.iuh.fit.authservice.entity.request.VerifyOtpRequest;
import vn.edu.iuh.fit.authservice.entity.response.LoginResponse;
import vn.edu.iuh.fit.authservice.exception.InvalidCredentialsException;
import vn.edu.iuh.fit.authservice.exception.PasswordMismatchException;
import vn.edu.iuh.fit.authservice.exception.UserAlreadyExistsException;
import vn.edu.iuh.fit.authservice.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public LoginResponse login(LoginRequest loginRequest) {
        ResponseEntity<UserAuthDTO> response = userClient.getAuthInfo(loginRequest.getCredential());

        if (response == null || response.getBody() == null) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        UserAuthDTO userAuth = response.getBody();

        if (!passwordEncoder.matches(loginRequest.getPassword(), userAuth.getHashedPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Get user information
        ResponseEntity<UserDTO> userResponse = userClient.getUserInfo(userAuth.getCredential());

        if (userResponse == null || userResponse.getBody() == null) {
            throw new UserNotFoundException("User not found with credential: " + loginRequest.getCredential());
        }

        // Create JWT
        String jwt = jwtService.generateToken(userAuth.getCredential(), userResponse.getBody().getRoles());

        return new LoginResponse(jwt);
    }


    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        try {
            userClient.createUser(request);
        } catch (FeignException.Conflict ex) {
            throw new UserAlreadyExistsException("User already exists");
        }

        otpService.sendOtp(request.getCredential());
    }

    public void verifyOtp(VerifyOtpRequest request) {

        // Verify OTP
        if (!otpService.verifyOtp(request.getCredential(), request.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }


    }


}
