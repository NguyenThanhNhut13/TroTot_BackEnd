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
import jakarta.ws.rs.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.userservice.enumeraion.Gender;
import vn.edu.iuh.fit.userservice.exception.UserNotFoundException;
import vn.edu.iuh.fit.userservice.mapper.UserProfileMapper;
import vn.edu.iuh.fit.userservice.model.dto.reponse.UserProfileResponse;
import vn.edu.iuh.fit.userservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.exception.UserAlreadyExistsException;
import vn.edu.iuh.fit.userservice.model.dto.request.UpdateUserProfileRequest;
import vn.edu.iuh.fit.userservice.model.entity.UserProfile;
import vn.edu.iuh.fit.userservice.repository.UserProfileRepository;

import java.time.LocalDateTime;

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


}
