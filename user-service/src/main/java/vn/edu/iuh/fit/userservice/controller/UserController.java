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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.userservice.model.dto.reponse.BaseResponse;
import vn.edu.iuh.fit.userservice.model.dto.reponse.UserProfileResponse;
import vn.edu.iuh.fit.userservice.model.dto.request.AddPostSlotRequest;
import vn.edu.iuh.fit.userservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.model.dto.request.UpdateUserProfileRequest;
import vn.edu.iuh.fit.userservice.service.UserProfileService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createUserInfo(@RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Add user profile success!", null)
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo() {
        UserProfileResponse userDTO = userService.getUserProfile();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "User profile retrieved successfully!", userDTO)
        );
    }

    @PutMapping
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateUserProfileRequest request) {
        UserProfileResponse updatedProfile = userService.updateUserProfile(request);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "User profile updated successfully!", updatedProfile)
        );
    }

    @PostMapping("/{userId}/add-posts")
    public ResponseEntity<BaseResponse<Integer>> addPostSlots(@PathVariable Long userId,
                                          @Valid @RequestBody AddPostSlotRequest request) {
        int newTotalPosts = userService.addPostSlots(userId, request.getAmount());

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Post slots updated successfully!", newTotalPosts)
        );
    }

    @PostMapping("/{userId}/use-post-slot")
    public ResponseEntity<BaseResponse<Integer>> usePostSlot(@PathVariable Long userId) {
        int remainingPosts = userService.usePostSlot(userId);

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Post slot used successfully!", remainingPosts)
        );
    }


}
