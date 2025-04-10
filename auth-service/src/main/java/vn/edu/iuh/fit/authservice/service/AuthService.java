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

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.authservice.client.UserClient;
import vn.edu.iuh.fit.authservice.exception.*;
import vn.edu.iuh.fit.authservice.model.dto.request.LoginRequest;
import vn.edu.iuh.fit.authservice.model.dto.request.RegisterProfileRequest;
import vn.edu.iuh.fit.authservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.authservice.model.dto.request.VerifyOtpRequest;
import vn.edu.iuh.fit.authservice.model.dto.response.LoginResponse;
import vn.edu.iuh.fit.authservice.model.dto.response.TokenResponse;
import vn.edu.iuh.fit.authservice.model.entity.Role;
import vn.edu.iuh.fit.authservice.model.entity.User;
import vn.edu.iuh.fit.authservice.repository.UserRepository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final UserClient userClient;
    private final RoleService roleService;
    private final TokenRedisService tokenRedisService;

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findUserByEmailOrPhoneNumber(loginRequest.getCredential());

        if (user == null) {
            throw new UserNotFoundException("User not found with credential: " + loginRequest.getCredential());
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        if (!user.isVerified()) {
            throw new UserNotVerifiedException("User account is not verified. Please verify your email before logging in.");
        }

        if (tokenRedisService.getRefreshTokenByUserId(user.getId()) != null) {
            throw new UnauthorizedException("You are already logged in.");
        }

        // Create JWT
        String jit = UUID.randomUUID().toString();
        String accessToken = jwtService.generateToken(user, false, jit);
        String refreshToken = jwtService.generateToken(user, true, jit);

        tokenRedisService.saveRefreshToken(jit, refreshToken, 7, TimeUnit.DAYS);
        tokenRedisService.saveRefreshTokenByUserId(user.getId(), jit, 7, TimeUnit.DAYS);

        return new LoginResponse(accessToken, refreshToken);
    }


    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        if (userRepository.existsByEmailOrPhoneNumber(request.getCredential())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = new User();
        if (request.getCredential().contains("@")) {
            user.setEmail(request.getCredential());
        } else {
            user.setPhoneNumber(request.getCredential());
        }

        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setVerified(false);
        User savedUser = userRepository.save(user);

        userClient.createUserInfo(new RegisterProfileRequest(savedUser.getId(), request.getFullName()));

        otpService.sendOtp(request.getCredential());
    }

    public void verifyOtp(VerifyOtpRequest request) {
        otpService.verifyOtp(request.getCredential(), request.getOtp());
        User user = userRepository.findUserByEmailOrPhoneNumber(request.getCredential());
        if (user == null) {
            throw new UnauthorizedException("Invalid credential");
        }
        user.setVerified(true);
        Role role = roleService.getRoleByName("USER");

        user.setRoles(new HashSet<>(List.of(role)));
        userRepository.save(user);
    }

    public TokenResponse refreshAccessToken(String accessToken, String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BadRequestException("Refresh token is missing");
        }

        if (accessToken == null || accessToken.isEmpty()) {
            throw new BadRequestException("Access token is missing");
        }

        if (!jwtService.validateToken(refreshToken) ||
                !tokenRedisService.isRefreshTokenSaved(jwtService.extractId(refreshToken))) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String userId = jwtService.extractSubject(refreshToken);
        Optional<User> user = userRepository.findById(Long.parseLong(userId));

        if (user.isEmpty()) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        // Check access token
        if (jwtService.validateToken(accessToken)) {
            String jit = jwtService.extractId(accessToken);

            // If not in blacklist then save
            if (!tokenRedisService.isAccessTokenBlacklisted(jit)) {
                long ttl = jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
                tokenRedisService.blacklistAccessToken(jit, ttl, TimeUnit.MILLISECONDS);
            }
        }

        // Remove old refreshToken
        tokenRedisService.deleteRefreshToken(jwtService.extractId(refreshToken));

        // Create new token
        String jit = UUID.randomUUID().toString();
        String newAccessToken = jwtService.generateToken(user.get(), false, jit);
        String newRefreshToken = jwtService.generateToken(user.get(), true, jit);

        tokenRedisService.saveRefreshToken(jit, refreshToken, 7, TimeUnit.DAYS);
        tokenRedisService.saveRefreshTokenByUserId(user.get().getId(), jit, 7, TimeUnit.DAYS);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String accessToken, String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BadRequestException("Refresh token is missing!");
        }

        if (tokenRedisService.isAccessTokenBlacklisted(jwtService.extractId(accessToken))) {
            throw new UnauthorizedException("You are logged out!");
        }

        // Check refreshToken
        if (!jwtService.validateToken(refreshToken) ||
                !tokenRedisService.isRefreshTokenSaved(jwtService.extractId(refreshToken))) {
            throw new UnauthorizedException("Invalid or expired refresh token!");
        }

        String jit = jwtService.extractId(accessToken);
        long ttl = jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis();
        tokenRedisService.blacklistAccessToken(jit, ttl, TimeUnit.MILLISECONDS);

        // Remove refreshToken
        tokenRedisService.deleteRefreshToken(jwtService.extractId(refreshToken));
        tokenRedisService.deleteRefreshTokenByUserId(Long.parseLong(jwtService.extractSubject(refreshToken)));
    }

//    public UserResponse getUserDTOById(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Chuyển danh sách role name thành Set<Role> tại đây
//        List<String> roleNames = userMapper.mapRoles(user.getRoles());
//        Set<Role> roles = roleNames.stream()
//                .map(roleService::getRoleByName)  // Lấy Role từ tên
//                .collect(Collectors.toSet());
//
//        // Tạo UserDTO từ User và cung cấp roles
//        UserProfileResponse userDTO = userMapper.toDTO(user);
//        userDTO.setRoles(roleNames);  // Set lại roles ở đây hoặc trực tiếp chuyển roles vào DTO.
//
//        return userDTO;
//    }


}
