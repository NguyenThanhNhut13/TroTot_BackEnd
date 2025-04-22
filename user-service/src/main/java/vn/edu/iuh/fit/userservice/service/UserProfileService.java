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
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.userservice.client.PaymentClient;
import vn.edu.iuh.fit.userservice.client.RoomClient;
import vn.edu.iuh.fit.userservice.enumeraion.Gender;
import vn.edu.iuh.fit.userservice.exception.BadRequestException;
import vn.edu.iuh.fit.userservice.exception.PaymentFailedException;
import vn.edu.iuh.fit.userservice.exception.UserNotFoundException;
import vn.edu.iuh.fit.userservice.mapper.UserProfileMapper;
import vn.edu.iuh.fit.userservice.model.dto.reponse.UserProfileResponse;
import vn.edu.iuh.fit.userservice.model.dto.request.DeductRequest;
import vn.edu.iuh.fit.userservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.exception.UserAlreadyExistsException;
import vn.edu.iuh.fit.userservice.model.dto.request.UpdateUserProfileRequest;
import vn.edu.iuh.fit.userservice.model.entity.UserProfile;
import vn.edu.iuh.fit.userservice.model.entity.Wishlist;
import vn.edu.iuh.fit.userservice.repository.UserProfileRepository;
import vn.edu.iuh.fit.userservice.repository.WishlistRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final PaymentClient paymentClient;
    private final RoomClient roomClient;
    private final WishlistRepository wishlistRepository;

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


    public int addPostSlots(int amount) {
        UserProfile user = getCurrentUser();

        if (amount <= 0) {
            throw new BadRequestException("Amount must be greater than 0");
        }

        Map<Integer, Integer> priceMap = Map.of(
                1, 20000,
                5, 90000,
                10, 170000
        );

        Integer price = priceMap.get(amount);
        if (price == null) {
            throw new BadRequestException("Only allowed to buy 1, 5 or 10 slots.");
        }

        // Gọi payment-service để trừ tiền
        DeductRequest request = DeductRequest.builder()
                .amount((long) price)
                .userId(user.getId())
                .description("User " + user.getId() + " purchased " + amount + " post slot(s).")
                .build();

        try {
            paymentClient.deduct(request);
        } catch (Exception e) {
            throw new PaymentFailedException("Unable to deduct payment. Please try again later.");
        }

        int currentPosts = user.getNumberOfPosts() != null ? user.getNumberOfPosts() : 0;
        user.setNumberOfPosts(currentPosts + amount);
        user.setUpdatedAt(LocalDateTime.now());

        userProfileRepository.save(user);
        return user.getNumberOfPosts();
    }

    public int usePostSlot() {
        UserProfile user = getCurrentUser();

        if (user.getNumberOfPosts() == null || user.getNumberOfPosts() <= 0) {
            throw new BadRequestException("No post slots available.");
        }

        user.setNumberOfPosts(user.getNumberOfPosts() - 1);
        user.setUpdatedAt(LocalDateTime.now());

        userProfileRepository.save(user);
        return user.getNumberOfPosts();
    }

    public void addRoomToWishlist(Long roomId) {
        UserProfile user = getCurrentUser();

        try {
            roomClient.checkRoomExists(roomId);
        } catch (NotFoundException e) {
            throw new BadRequestException("Room does not exist!");
        }

        boolean exists = wishlistRepository.existsByUserProfileIdAndRoomId(user.getId(), roomId);

        if (exists) {
            throw new BadRequestException("Room already exists in wishlist!");
        }

        Wishlist newWishList = Wishlist.builder()
                .roomId(roomId)
                .userProfile(user)
                .savedAt(LocalDateTime.now())
                .build();

        wishlistRepository.save(newWishList);
    }

    public List<Wishlist> getSavedRooms() {
        UserProfile userProfile = getCurrentUser();
        return wishlistRepository.findByUserProfileId(userProfile.getId());
    }


    public void removeRoomFromWishlist(Long roomId) {
        UserProfile user = getCurrentUser();

        try {
            roomClient.checkRoomExists(roomId);
        } catch (NotFoundException e) {
            throw new BadRequestException("Room does not exist!");
        }

        Wishlist wishlist = wishlistRepository.findByUserProfileIdAndRoomId(user.getId(), roomId)
                .orElseThrow(() -> new BadRequestException("Room not found in wishlist!"));

        wishlistRepository.delete(wishlist);
    }

}
