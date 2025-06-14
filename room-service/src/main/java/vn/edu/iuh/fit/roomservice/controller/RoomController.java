/*
 * @ (#) UserController.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.controller;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.roomservice.client.AddressClient;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;
import vn.edu.iuh.fit.roomservice.model.dto.*;
import vn.edu.iuh.fit.roomservice.model.dto.request.PushNotificationRequest;
import vn.edu.iuh.fit.roomservice.model.dto.request.VideoReviewRequest;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;
import vn.edu.iuh.fit.roomservice.model.dto.response.PageResponse;
import vn.edu.iuh.fit.roomservice.service.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final AmenityService amenityService;
    private final SurroundingAreaService surroundingAreaService;
    private final TargetAudienceService targetAudienceService;
    private final PushNotificationProducer pushNotificationProducer;
    private final AddressClient addressClient;

    //    Nguyễn Quân - Notification - Room service
    private final KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping
    @RateLimiter(name = "saveRoomLimit")
    public ResponseEntity<?> saveRoom(@RequestBody RoomDTO room) {
        RoomDTO savedRoom = roomService.saveRoom(room);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Room saved successfully.", savedRoom)
        );
    }

    @PutMapping("/{id}")
    @RateLimiter(name = "updateRoomLimit")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody RoomDTO room) {
        RoomDTO updatedRoom = roomService.updateRoom(id, room);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Room updated successfully.", updatedRoom)
        );
    }

    @GetMapping("/amenities")
    @RateLimiter(name = "staticDataLimit")
    public ResponseEntity<BaseResponse<List<AmenityDTO>>> getAllAmenities() {
        List<AmenityDTO> data = amenityService.getAmenities();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get the list of amenities successfully", data)
        );
    }

    @GetMapping("/surrounding-areas")
    @RateLimiter(name = "staticDataLimit")
    public ResponseEntity<BaseResponse<List<SurroundingAreaDTO>>> getAllSurroundingAreas() {
        List<SurroundingAreaDTO> data = surroundingAreaService.getAllSurroundingAreas();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get surrounding areas list successfully", data)
        );
    }

    @GetMapping("/target-audiences")
    @RateLimiter(name = "staticDataLimit")
    public ResponseEntity<BaseResponse<List<TargetAudienceDTO>>> getAllTargetAudiences() {
        List<TargetAudienceDTO> data = targetAudienceService.getAllTargetAudiences();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get list of target audiences successfully", data)
        );
    }

    @GetMapping
    @RateLimiter(name = "findAllRoomsLimit")
    public ResponseEntity<BaseResponse<PageResponse<RoomListDTO>>> findAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) RoomType roomType
    ) {
        PageResponse<RoomListDTO> pagedResponse = roomService.findAllRooms(page, size, sort, roomType);

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get all room successful", pagedResponse)
        );
    }

    @GetMapping("/{id}")
    @RateLimiter(name = "getRoomByIdLimit")
    public ResponseEntity<BaseResponse<RoomDTO>> findById(@PathVariable Long id) {
        RoomDTO data = roomService.findById(id);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get room successful", data)
        );
    }

    @GetMapping("/search")
    @RateLimiter(name = "searchRoomsLimit")
    public ResponseEntity<BaseResponse<PageResponse<RoomListDTO>>> searchRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,

            @RequestParam(required = false) String street,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String city,

            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String areaRange,
            @RequestParam(required = false) String roomType,

            @RequestParam(required = false) List<String> amenities,
            @RequestParam(required = false) List<String> environment,
            @RequestParam(required = false) List<String> targetAudience,

            @RequestParam(required = false) Boolean hasVideoReview
    ) {
        PageResponse<RoomListDTO> response = roomService.searchRooms(
                page, size, sort,
                street, district, city,
                minPrice, maxPrice, areaRange, roomType,
                amenities, environment, targetAudience, hasVideoReview
        );

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Search room successfully", response)
        );
    }

    @GetMapping("/export")
    public ResponseEntity<BaseResponse<List<RoomTrainDTO>>> exportAllRooms() {
        List<RoomTrainDTO> pagedResponse = roomService.exportAllRooms();

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get room train successful", pagedResponse)
        );
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<BaseResponse<Boolean>> checkRoomExists(@PathVariable Long id) {
        boolean exists = roomService.checkRoomExistsById(id);

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Room exists!", exists)
        );
    }

    @PostMapping("/bulk")
    @RateLimiter(name = "findByIdsLimit")
    public ResponseEntity<BaseResponse<List<RoomListDTO>>> findByIds(@RequestBody List<Long> ids) {
        List<RoomListDTO> data = roomService.findByIds(ids);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get rooms successful", data)
        );
    }

    @PutMapping("/{roomId}/video-review")
    public ResponseEntity<BaseResponse<Void>> updateVideoReview(@PathVariable Long roomId,
                                                                @RequestBody VideoReviewRequest request) {
        roomService.updateVideoReview(roomId, request.getVideoUrl());
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Video review updated successfully.", null)
        );
    }

    @GetMapping("/test-kafka")
    public ResponseEntity<String> testKafka() {
        kafkaTemplate.send("push-notification", "Test", "Hello from room-service!");
        return ResponseEntity.ok("Sent!");
    }

    @PostMapping("/notify")
    public ResponseEntity<BaseResponse<String>> sendPushNotification(
            @RequestBody PushNotificationRequest request
    ) {
        pushNotificationProducer.sendNotification(request);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Gửi thông báo thành công", "Đã gửi push notification đến Kafka topic")
        );
    }

    @GetMapping("/test-address-retry")
    public ResponseEntity<String> testAddressRetry() {
        ResponseEntity<BaseResponse<String>> response = addressClient.testRetry();
        if (response.getStatusCode().is2xxSuccessful() && Objects.requireNonNull(response.getBody()).isSuccess()) {
            return ResponseEntity.ok("Success: " + response.getBody().getData());
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Failed: " + Objects.requireNonNull(response.getBody()).getMessage());
        }
    }

    @GetMapping("/by-user")
    @RateLimiter(name = "getRoomByUserLimit")
    public ResponseEntity<BaseResponse<PageResponse<RoomListDTO>>> getRoomByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        PageResponse<RoomListDTO> pagedRooms = roomService.getRoomByUser(page, size, sort);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get user rooms successful", pagedRooms)
        );
    }

}
