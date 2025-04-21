/*
 * @ (#) UserService.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import vn.edu.iuh.fit.userservice.enumeraion.Gender;
import vn.edu.iuh.fit.userservice.exception.BadRequestException;
import vn.edu.iuh.fit.userservice.exception.UserNotFoundException;
import vn.edu.iuh.fit.userservice.mapper.UserProfileMapper;
import vn.edu.iuh.fit.userservice.model.dto.reponse.UserProfileResponse;
import vn.edu.iuh.fit.userservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.exception.UserAlreadyExistsException;
import vn.edu.iuh.fit.userservice.model.dto.request.UpdateUserProfileRequest;
import vn.edu.iuh.fit.userservice.model.entity.UserProfile;
import vn.edu.iuh.fit.userservice.repository.UserProfileRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    public void createUser(RegisterRequest request) {
        if (userRepository.existsUserProfilesById(request.getId())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        UserProfile userProfile = new UserProfile();

        userProfile.setId(request.getId());
        userProfile.setFullName(request.getFullName());
        userRepository.save(userProfile);
    }

    public UserProfileResponse getUserProfile() {
        try {
            UserProfile userProfile = getCurrentUser();
            return userProfileMapper.toDTO(userProfile);

        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to fetch user profile", e);
        }
    }

    public UserProfileResponse updateUserProfile(UpdateUserProfileRequest request) {
        if (request.getFullName() != null && request.getFullName().length() > 255) {
            throw new ValidationException("Full name cannot exceed 255 characters");
        }

        if (request.getDob() != null && request.getDob().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Date of birth cannot be in the future");
        }

        if (request.getCccd() != null && !request.getCccd().matches("\\d{12}")) {
            throw new ValidationException("CCCD must be a 12-digit number");
        }

        if (request.getGender() != null && !Gender.isValid(request.getGender())) {
            throw new ValidationException("Invalid gender");
        }

        UserProfile user = getCurrentUser();
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getDob() != null) {
            user.setDob(request.getDob());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getCccd() != null) {
            user.setCccd(request.getCccd());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        return userProfileMapper.toDTO(user);
    }

    private UserProfile getCurrentUser() {
        Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principalObj instanceof String principal)) {
            throw new IllegalStateException("Invalid principal type");
        }

        long userId;

        try {
            userId = Long.parseLong(principal);
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Principal is not a valid user ID", ex);
        }

        return userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }


    public int addPostSlots(Long userId, int amount) {
        if (amount <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        int currentPosts = user.getNumberOfPosts() != null ? user.getNumberOfPosts() : 0;
        user.setNumberOfPosts(currentPosts + amount);
        user.setUpdatedAt(LocalDateTime.now());

        userProfileRepository.save(user);
        return user.getNumberOfPosts();
    }

    public int usePostSlot(Long userId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (user.getNumberOfPosts() == null || user.getNumberOfPosts() <= 0) {
            throw new BadRequestException("No post slots available.");
        }

        user.setNumberOfPosts(user.getNumberOfPosts() - 1);
        user.setUpdatedAt(LocalDateTime.now());

        userProfileRepository.save(user);
        return user.getNumberOfPosts();
    }

    public Map<String, Object> purchasePostSlots(Long userId, int amount, String bearerToken) {
        int price = switch (amount) {
            case 1 -> 20000;
            case 5 -> 90000;
            case 10 -> 170000;
            default -> throw new BadRequestException("Chỉ cho phép mua 1, 5 hoặc 10 trọ.");
        };

        // Truyền token khi gọi trừ tiền
        boolean isPaid = callDeductApi(userId, price, bearerToken);

        if (!isPaid) {
            throw new InternalServerErrorException("Thanh toán thất bại. Vui lòng kiểm tra số dư.");
        }

        int newTotalPosts = addPostSlots(userId, amount);

        Map<String, Object> result = new HashMap<>();
        result.put("newTotalPosts", newTotalPosts);
        result.put("amountAdded", amount);
        result.put("amountPaid", price);

        return result;
    }


    private boolean callDeductApi(Long userId, int amount, String bearerToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8222/api/v1/payments/deduct";

        Map<String, Object> body = new HashMap<>();
        body.put("userId", userId);
        body.put("amount", amount);
        body.put("description", "Trừ tiền mua lượt đăng trọ");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        System.out.println("🔑 Token gửi sang payment: " + bearerToken);
        headers.setBearerAuth(bearerToken.replace("Bearer ", ""));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int consumePostSlot(Long userId) {
        UserProfile user = userProfileRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng có ID: " + userId));

        Integer current = user.getNumberOfPosts();
        if (current == null || current <= 0) {
            throw new BadRequestException("Bạn không còn lượt đăng trọ nào.");
        }

        user.setNumberOfPosts(current - 1);
        user.setUpdatedAt(LocalDateTime.now());
        userProfileRepository.save(user);

        return user.getNumberOfPosts();
    }

}
