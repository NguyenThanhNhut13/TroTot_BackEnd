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
import vn.edu.iuh.fit.roomservice.model.dto.AmenityDTO;
import vn.edu.iuh.fit.roomservice.model.dto.RoomDTO;
import vn.edu.iuh.fit.roomservice.model.dto.SurroundingAreaDTO;
import vn.edu.iuh.fit.roomservice.model.dto.TargetAudienceDTO;
import vn.edu.iuh.fit.roomservice.model.dto.response.BaseResponse;
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
                new BaseResponse<>(true, "Get list of successful rental objects", data)
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<RoomDTO>>> findAllRooms() {
        List<RoomDTO> data = roomService.findAllRooms();
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get all room successful", data)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<RoomDTO>> findById(@PathVariable Long id) {
        RoomDTO data = roomService.findById(id);
        return ResponseEntity.ok(
                new BaseResponse<>(true, "Get room successful", data)
        );
    }

    @GetMapping("/by-addresses")
    public ResponseEntity<List<RoomDTO>> findAllRooms(
            @RequestParam(required = false) String street,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String city
    ) {
        return ResponseEntity.ok(roomService.findRoomsByAddress(street, district, city));
    }

}
