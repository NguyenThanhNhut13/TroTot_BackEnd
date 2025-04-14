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
import vn.edu.iuh.fit.roomservice.client.AddressClient;
import vn.edu.iuh.fit.roomservice.mapper.ImageMapper;
import vn.edu.iuh.fit.roomservice.mapper.RoomMapper;
import vn.edu.iuh.fit.roomservice.model.dto.*;
import vn.edu.iuh.fit.roomservice.model.entity.Room;
import vn.edu.iuh.fit.roomservice.repository.RoomRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final AddressClient addressClient;
    private final RoomMapper roomMapper;
    private final ImageMapper imageMapper;

    public RoomDTO saveRoom(RoomDTO roomDTO) {
        Room room = roomMapper.toEntity(roomDTO);
        Room savedRoom = roomRepository.save(room);
        return roomMapper.toDTO(savedRoom);
    }

    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    public List<RoomDTO> findRoomsByAddress(String street, String district, String city) {
        List<AddressDTO> addressDTOS = addressClient.search(street, district, city).getBody();

        assert addressDTOS != null;
        if (addressDTOS.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> addressIds = addressDTOS.stream().map(AddressDTO::getId).collect(Collectors.toList());
        List<Room> rooms = roomRepository.findByAddressIdIn(addressIds);

        return rooms.stream().map(room ->
                RoomDTO.builder()
                        .id(room.getId())
                        .userId(room.getUserId())
                        .title(room.getTitle())
                        .description(room.getDescription())
                        .price(room.getPrice())
                        .area(room.getArea())
                        .images(imageMapper.toDTOs(room.getImages()))
                        .status(room.getStatus())
                        .amenities(room.getAmenities().stream()
                                .map(a -> new AmenityDTO(a.getId(), a.getName()))
                                .collect(Collectors.toList()))
                        .environments(room.getSurroundingAreas().stream()
                                .map(e -> new SurroundingAreaDTO(e.getId(), e.getName()))
                                .collect(Collectors.toList()))
                        .targetAudiences(room.getTargetAudiences().stream()
                                .map(t -> new TargetAudienceDTO(t.getId(), t.getName()))
                                .collect(Collectors.toList()))
                        .createdAt(room.getCreatedAt())
                        .updatedAt(room.getUpdatedAt())
                        .address(addressDTOS.stream()
                                .filter(a -> a.getId().equals(room.getAddressId()))
                                .findFirst()
                                .orElse(null)) // Gán đúng AddressDTO
                        .build()
        ).collect(Collectors.toList());

    }
}
