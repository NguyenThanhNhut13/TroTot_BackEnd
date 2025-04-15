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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.fit.roomservice.dto.RoomDTO;
import vn.edu.iuh.fit.roomservice.entity.Room;
import vn.edu.iuh.fit.roomservice.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody Room room) {
        roomService.saveRoom(room);
    }

    @GetMapping
    public ResponseEntity<List<Room>> findAllRooms() {
        return ResponseEntity.ok(roomService.findAllRooms());
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
