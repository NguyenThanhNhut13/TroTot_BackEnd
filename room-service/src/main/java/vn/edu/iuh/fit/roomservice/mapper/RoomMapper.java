/*
 * @ (#) RoomMapper.java       1.0     11/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.mapper;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 11/04/2025
 * @version:    1.0
 */

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.edu.iuh.fit.roomservice.model.dto.RoomDTO;
import vn.edu.iuh.fit.roomservice.model.dto.RoomListDTO;
import vn.edu.iuh.fit.roomservice.model.entity.Image;
import vn.edu.iuh.fit.roomservice.model.entity.Room;
import vn.edu.iuh.fit.roomservice.model.entity.RoomDetail;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {
        AmenityMapper.class,
        SurroundingAreaMapper.class,
        TargetAudienceMapper.class,
        ImageMapper.class
})
public interface RoomMapper {

    @Mapping(target = "numberOfLivingRooms", source = "roomDetail.numberOfLivingRooms")
    @Mapping(target = "numberOfKitchens", source = "roomDetail.numberOfKitchens")
    @Mapping(target = "numberOfBathrooms", source = "roomDetail.numberOfBathrooms")
    @Mapping(target = "numberOfBedrooms", source = "roomDetail.numberOfBedrooms")
    @Mapping(source = "selfManaged", target = "selfManaged")
    RoomDTO toDTO(Room room);

    @Mapping(target = "roomDetail", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(source = "selfManaged", target = "selfManaged")
    Room toEntity(RoomDTO roomDTO);

    @Mapping(target = "district", ignore = true)
    @Mapping(target = "province", ignore = true)
    @Mapping(target = "imageUrls", ignore = true)
    RoomListDTO toListDTO(Room room);

    @AfterMapping
    default void setRoomDetail(@MappingTarget Room room, RoomDTO dto) {
        if (dto.getNumberOfLivingRooms() != null || dto.getNumberOfKitchens() != null ||
                dto.getNumberOfBathrooms() != null || dto.getNumberOfBedrooms() != null) {

            RoomDetail detail = RoomDetail.builder()
                    .room(room)
                    .numberOfLivingRooms(dto.getNumberOfLivingRooms() != null ? dto.getNumberOfLivingRooms() : 0)
                    .numberOfKitchens(dto.getNumberOfKitchens() != null ? dto.getNumberOfKitchens() : 0)
                    .numberOfBathrooms(dto.getNumberOfBathrooms() != null ? dto.getNumberOfBathrooms() : 0)
                    .numberOfBedrooms(dto.getNumberOfBedrooms() != null ? dto.getNumberOfBedrooms() : 0)
                    .build();

            room.setRoomDetail(detail);
        }
    }

    @AfterMapping
    default void extractImageUrls(@MappingTarget RoomListDTO dto, Room room) {
        if (room.getImages() != null && !room.getImages().isEmpty()) {
            // Extract only imageUrl from each Image object
            dto.setImageUrls(room.getImages().stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList()));
        }
    }
}
