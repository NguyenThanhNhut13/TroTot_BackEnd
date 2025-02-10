/*
 * @ (#) UserService.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.roomservice.entity.Room;
import vn.edu.iuh.fit.roomservice.repository.RoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public void saveRoom(Room room) {
        roomRepository.save(room);
    }

    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }
}
