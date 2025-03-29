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
import vn.edu.iuh.fit.authservice.model.entity.Role;
import vn.edu.iuh.fit.authservice.model.entity.User;
import vn.edu.iuh.fit.authservice.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final UserClient userClient;

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

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        // Create JWT
        String jwt = jwtService.generateToken(loginRequest.getCredential(), roleNames);

        return new LoginResponse(jwt);
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

        // Verify OTP
        if (!otpService.verifyOtp(request.getCredential(), request.getOtp())) {
            throw new IllegalArgumentException("Invalid OTP");
        }


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
