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
import vn.edu.iuh.fit.authservice.enumerate.OtpPurpose;
import vn.edu.iuh.fit.authservice.exception.*;
import vn.edu.iuh.fit.authservice.model.dto.request.*;
import vn.edu.iuh.fit.authservice.model.dto.response.AccountInfoResponse;
import vn.edu.iuh.fit.authservice.model.dto.response.CredentialStatus;
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
            throw new InvalidCredentialException("Invalid credentials");
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

        // Validate data
        validateRegistrationData(request);

        // Check user if exists
        if (userRepository.existsByEmailOrPhoneNumber(request.getCredential())) {
            throw new UserAlreadyExistException("User already exists");
        }

        // Create and save new user
        User savedUser = createAndSaveUser(request);

        try {
            userClient.createUserInfo(new RegisterProfileRequest(savedUser.getId(), request.getFullName()));
        } catch (Exception ex) {
            userRepository.deleteById(savedUser.getId());
            throw new RuntimeException("Failed to create user profile", ex);
        }

        otpService.sendOtp(request.getCredential(), OtpPurpose.REGISTER);
    }

    /**
     * Validate register data
     */
    private void validateRegistrationData(RegisterRequest request) {
        String credential = request.getCredential();
        String fullName = request.getFullName();
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        // Check null or empty
        if (credential == null || credential.isEmpty()) {
            throw new InvalidCredentialException("Credential cannot be empty");
        }

        if (fullName == null || fullName.isEmpty()) {
            throw new InvalidCredentialException("Full name cannot be empty");
        }

        if (password == null || password.isEmpty()) {
            throw new InvalidCredentialException("Password cannot be empty");
        }

        // Check password match
        if (!password.equals(confirmPassword)) {
            throw new PasswordMismatchException("Passwords do not match");
        }

        // Check credential
        if (credential.contains("@")) {
            if (!isValidEmail(credential)) {
                throw new InvalidCredentialException("Invalid email format");
            }
        } else {
            if (!isValidPhone(credential)) {
                throw new InvalidCredentialException("Invalid phone number format");
            }
        }

        // Check fullName
        if (!isValidName(fullName)) {
            throw new InvalidCredentialException("Invalid full name format");
        }
    }

    /**
     * Create and save new user
     */
    private User createAndSaveUser(RegisterRequest request) {
        User user = new User();

        // Set up email or phone number
        if (request.getCredential().contains("@")) {
            user.setEmail(request.getCredential());
        } else {
            user.setPhoneNumber(request.getCredential());
        }

        // Encrypt passwords and set authentication status
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setVerified(false);

        return userRepository.save(user);
    }

    public void verifyOtp(VerifyOtpRequest request) {
        otpService.verifyOtp(request.getCredential(), request.getOtp(), OtpPurpose.REGISTER);
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


    /**
     * Check the validity of the email address according to RFC 5322
     * @param email The email address to check
     * @return true if the email is valid, false if not valid
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Check the validity of the phone number
     * Support international and Vietnamese formats
     * @param phone The phone number to check
     * @return true if the phone number is valid, false if invalid
     */
    private boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) return false;

        String cleanPhone = phone.replaceAll("[\\s\\-()]", "");

        // International format with + sign
        if (cleanPhone.startsWith("+"))
            return cleanPhone.substring(1).matches("\\d{9,15}");

        // Vietnam phone number: 10 digits, starting with 0
        if (cleanPhone.startsWith("0") && cleanPhone.length() == 10)
            return true;

        // International Vietnam phone number: 84 + 9 digits
        if (cleanPhone.startsWith("84") && cleanPhone.length() == 11)
            return true;

        // Other cases: 9-15 numbers
        return cleanPhone.matches("\\d{9,15}");
    }

    /**
     * Check the validity of the username
     * Support Vietnamese names with accents and other languages
     * @param name The username to check
     * @return true if the name is valid, false if invalid
     */
    private boolean isValidName(String name) {
        if (name == null || name.isEmpty()) return false;

        String trimmedName = name.trim();

        // Check length and format
        return trimmedName.length() >= 2 &&
                trimmedName.length() <= 50 &&
                trimmedName.matches("^[\\p{L}\\s'-]+$") &&
                !trimmedName.contains("  ") &&
                !trimmedName.contains("--") &&
                !trimmedName.contains("''");
    }

    public void forgotPasswordRequest(String credential) {
        if (credential == null || credential.isEmpty()) {
            throw new InvalidCredentialException("Credential cannot be empty!");
        }

        // Check credential
        if (credential.contains("@")) {
            if (!isValidEmail(credential)) {
                throw new InvalidCredentialException("Invalid email format!");
            }
        } else {
            if (!isValidPhone(credential)) {
                throw new InvalidCredentialException("Invalid phone number format!");
            }
        }

        if (!userRepository.existsByEmailOrPhoneNumber(credential)) {
            throw new UserNotFoundException("User not found!");
        }

        otpService.sendOtp(credential, OtpPurpose.FORGOT_PASSWORD);
    }

    public String verifyOtpAndGenerateToken(VerifyOtpRequest request) {
        otpService.verifyOtp(request.getCredential(), request.getOtp(), OtpPurpose.FORGOT_PASSWORD);
        String token = UUID.randomUUID().toString();
        tokenRedisService.saveResetPasswordToken(token, request.getCredential(), 5, TimeUnit.MINUTES);
        return token;
    }

    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equalsIgnoreCase(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match!");
        }
        String credential = tokenRedisService.getCredentialByResetPasswordToken(request.getToken());

        if (credential == null) {
            throw new UnauthorizedException("Invalid token!");
        }

        User user = userRepository.findUserByEmailOrPhoneNumber(credential);
        user.setPassword(BCrypt.hashpw(request.getNewPassword(), BCrypt.gensalt()));
        userRepository.save(user);

        tokenRedisService.deleteResetPasswordToken(request.getToken());
    }

    public AccountInfoResponse getAccountInfo(String authHeader) {
        Long userId = Long.parseLong(jwtService.extractSubject(authHeader.replace("Bearer ", "")));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));

        CredentialStatus email = buildCredentialStatus(user.getEmail(), tokenRedisService.getPendingCredential(userId, "email"));
        CredentialStatus phone = buildCredentialStatus(user.getPhoneNumber(), tokenRedisService.getPendingCredential(userId, "phone"));

        return AccountInfoResponse.builder()
                .userId(userId)
                .email(email)
                .phoneNumber(phone)
                .build();
    }

    private CredentialStatus buildCredentialStatus(String value, String pending) {
        return CredentialStatus.builder()
                .value(value)
                .pending(pending)
                .isVerified(pending == null && value != null && !value.isEmpty())
                .build();
    }

    public void updateCredential(String authHeader, CredentialUpdateRequest request) {
        Long userId = Long.parseLong(jwtService.extractSubject(authHeader.replace("Bearer ", "")));
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));

        String value = request.getValue();
        String type = request.getType();

        if (!type.equals("email") && !type.equals("phone")) {
            throw new InvalidCredentialException("Invalid credential type!");
        }

        // Validate format
        if (type.equals("email")) {
            if (!isValidEmail(value)) {
                throw new InvalidCredentialException("Invalid email format!");
            }
        } else {
            if (!isValidPhone(value)) {
                throw new InvalidCredentialException("Invalid phone number format!");
            }
        }

        // Check if value already exists
        if (userRepository.existsByEmailOrPhoneNumber(value)) {
            throw new CredentialAlreadyExistException("Credential already exists!");
        }

        // Save to Redis as pending
        tokenRedisService.savePendingCredentialUpdate(userId, type, value, 3, TimeUnit.DAYS);

        otpService.sendOtp(value, OtpPurpose.UPDATE_CREDENTIAL);
    }

    public void verifyCredential(String authHeader, CredentialVerifyRequest request) {
        Long userId = Long.parseLong(jwtService.extractSubject(authHeader.replace("Bearer ", "")));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found!"));

        String type = request.getType();
        String otp = request.getOtp();

        String pendingValue = tokenRedisService.getPendingCredential(userId, type);

        if (pendingValue == null) {
            throw new BadRequestException("No pending " + type + " found!");
        }

        otpService.verifyOtp(pendingValue, otp, OtpPurpose.UPDATE_CREDENTIAL);

        if (type.equals("email")) user.setEmail(pendingValue);
        else if (type.equals("phone")) user.setPhoneNumber(pendingValue);
        else throw new InvalidCredentialException("Invalid credential type!");
        userRepository.save(user);

        // Remove pending and OTP
        tokenRedisService.deletePendingCredential(userId, type);
    }

    public void resendOtp(ResendOtpRequest request) {
        String credential = request.getCredential();
        OtpPurpose purpose = request.getPurpose();

        if (credential == null || credential.trim().isEmpty()) {
            throw new InvalidCredentialException("Credential must not be empty");
        }

        if (purpose == null) {
            throw new BadRequestException("OTP purpose is required");
        }

        User user = userRepository.findUserByEmailOrPhoneNumber(credential);

        if (user == null) {
            throw new UserNotFoundException("User not found with given credential");
        }

        switch (purpose) {
            case REGISTER:
                if (user.isVerified()) {
                    throw new InvalidCredentialException("This account has already been verified");
                }
                break;

            case FORGOT_PASSWORD:
            case UPDATE_CREDENTIAL:
                if (!user.isVerified()) {
                    throw new InvalidCredentialException("This account is not verified yet");
                }
                break;

            default:
                throw new BadRequestException("Unsupported OTP purpose");
        }

        otpService.sendOtp(credential, purpose);
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
