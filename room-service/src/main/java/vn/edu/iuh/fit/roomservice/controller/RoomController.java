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

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;
import vn.edu.iuh.fit.roomservice.model.dto.AmenityDTO;
import vn.edu.iuh.fit.roomservice.model.dto.RoomDTO;
import vn.edu.iuh.fit.roomservice.model.dto.SurroundingAreaDTO;
import vn.edu.iuh.fit.roomservice.model.dto.TargetAudienceDTO;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;
import vn.edu.iuh.fit.roomservice.model.dto.response.PageResponse;
import vn.edu.iuh.fit.roomservice.model.entity.Room;
import vn.edu.iuh.fit.roomservice.service.AmenityService;
import vn.edu.iuh.fit.roomservice.service.RoomService;
import vn.edu.iuh.fit.roomservice.service.SurroundingAreaService;
import vn.edu.iuh.fit.roomservice.service.TargetAudienceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final AmenityService amenityService;
    private final SurroundingAreaService surroundingAreaService;
    private final TargetAudienceService targetAudienceService;

    @PostMapping
    public ResponseEntity<?> saveRoom(@RequestBody RoomDTO room) {
        RoomDTO savedRoom = roomService.saveRoom(room);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Room saved successfully.", savedRoom)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @RequestBody RoomDTO room) {
        RoomDTO updatedRoom = roomService.updateRoom(id, room);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Room updated successfully.", updatedRoom)
        );
    }

    @GetMapping("/amenities")
    public ResponseEntity<BaseResponse<List<AmenityDTO>>> getAllAmenities() {
        List<AmenityDTO> data = amenityService.getAmenities();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get the list of amenities successfully", data)
        );
    }

    @GetMapping("/surrounding-areas")
    public ResponseEntity<BaseResponse<List<SurroundingAreaDTO>>> getAllSurroundingAreas() {
        List<SurroundingAreaDTO> data = surroundingAreaService.getAllSurroundingAreas();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get surrounding areas list successfully", data)
        );
    }

    @GetMapping("/target-audiences")
    public ResponseEntity<BaseResponse<List<TargetAudienceDTO>>> getAllTargetAudiences() {
        List<TargetAudienceDTO> data = targetAudienceService.getAllTargetAudiences();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get list of target audiences successfully", data)
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PageResponse<RoomDTO>>> findAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) RoomType roomType
    ) {
        PageResponse<RoomDTO> pagedResponse = roomService.findAllRooms(page, size, sort, roomType);

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get all room successful", pagedResponse)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RoomDTO>> findById(@PathVariable Long id) {
        RoomDTO data = roomService.findById(id);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get room successful", data)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<PageResponse<RoomDTO>>> searchRooms(
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

            @RequestParam(required = false) List<Long> amenities,
            @RequestParam(required = false) List<Long> environment,
            @RequestParam(required = false) List<Long> targetAudience

//            @RequestParam(required = false) Boolean hasVideoReview
    ) {
        PageResponse<RoomDTO> response = roomService.searchRooms(
                page, size, sort,
                street, district, city,
                minPrice, maxPrice, areaRange, roomType,
                amenities, environment, targetAudience
        );

        return ResponseEntity.ok(
                new BaseResponse<>(true, "Search room successfully", response)
        );
    }


}
