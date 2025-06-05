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

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.userservice.model.dto.reponse.*;
import vn.edu.iuh.fit.userservice.model.dto.request.AddPostSlotRequest;
import vn.edu.iuh.fit.userservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.model.dto.request.UpdateUserProfileRequest;
import vn.edu.iuh.fit.userservice.model.entity.Wishlist;
import vn.edu.iuh.fit.userservice.service.UserProfileService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserProfileService userService;

    @PostMapping("/create")
    @RateLimiter(name = "userServiceRateLimiter")
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

    @PostMapping("/add-posts")
    public ResponseEntity<BaseResponse<Integer>> addPostSlots(@Valid @RequestBody AddPostSlotRequest request) {
        int newTotalPosts = userService.addPostSlots(request.getAmount());

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Post slots updated successfully!", newTotalPosts)
        );
    }

    @PostMapping("/use-post-slot")
    public ResponseEntity<BaseResponse<Integer>> usePostSlot() {
        int remainingPosts = userService.usePostSlot();

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Post slot used successfully!", remainingPosts)
        );
    }

    @PostMapping("/wish-list/{roomId}")
    public ResponseEntity<BaseResponse<String>> addToWishlist(
            @PathVariable Long roomId) {

        userService.addRoomToWishlist(roomId);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Room added to wishlist!", "Room ID: " + roomId)
        );
    }

    @GetMapping("/wish-list")
    public ResponseEntity<BaseResponse<List<RoomListResponse>>> getWishlist() {

        List<RoomListResponse> wishlist = userService.getSavedRooms();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get list wishlist successfully!", wishlist)
        );
    }

    @GetMapping("/wish-list/ids")
    public ResponseEntity<BaseResponse<UserWishlistIdsResponse>> getWishListIdByUser() {

        UserWishlistIdsResponse wishlistIds = userService.getWishListIdByUser();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Fetched wishlist room IDs successfully!", wishlistIds)
        );
    }

    @GetMapping("/{userId}/wish-list")
    public ResponseEntity<BaseResponse<UserWishlistResponse>> getWishlistByUserId(@PathVariable Long userId) {

        UserWishlistResponse wishlist = userService.getWishListByUserId(userId);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get list wishlist successfully!", wishlist)
        );
    }

    @GetMapping("/wish-list/all")
    public ResponseEntity<BaseResponse<List<UserWishlistResponse>>> getAllWishlist() {

        List<UserWishlistResponse> wishlist = userService.getAllWishList();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get all wishlist successfully!", wishlist)
        );
    }

    @DeleteMapping("/wish-list/{roomId}")
    public ResponseEntity<BaseResponse<String>> removeFromWishlist(
            @PathVariable Long roomId) {

        userService.removeRoomFromWishlist(roomId);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Delete room from wishlist!", "Room ID: " + roomId)
        );
    }

}
