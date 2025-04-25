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

import org.mapstruct.*;
import vn.edu.iuh.fit.roomservice.model.dto.RoomDTO;
import vn.edu.iuh.fit.roomservice.model.dto.RoomListDTO;
import vn.edu.iuh.fit.roomservice.model.dto.RoomTrainDTO;
import vn.edu.iuh.fit.roomservice.model.entity.*;

import java.util.Set;
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
    @Mapping(source = "roomType", target = "roomType")
    RoomListDTO toListDTO(Room room);

    @Mapping(target = "amenities", source = "amenities", qualifiedByName = "mapAmenityNames")
    @Mapping(target = "targetAudiences", source = "targetAudiences", qualifiedByName = "mapTargetAudienceNames")
    @Mapping(target = "surroundingAreas", source = "surroundingAreas", qualifiedByName = "mapSurroundingAreaNames")
    @Mapping(target = "province", ignore = true)
    @Mapping(target = "district", ignore = true)
    RoomTrainDTO toRoomTrainDTO(Room room);

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

    @Named("mapAmenityNames")
    static Set<String> mapAmenityNames(Set<Amenity> amenities) {
        return amenities != null
                ? amenities.stream().map(Amenity::getName).collect(Collectors.toSet())
                : null;
    }

    @Named("mapTargetAudienceNames")
    static Set<String> mapTargetAudienceNames(Set<TargetAudience> targetAudiences) {
        return targetAudiences != null
                ? targetAudiences.stream().map(TargetAudience::getName).collect(Collectors.toSet())
                : null;
    }

    @Named("mapSurroundingAreaNames")
    static Set<String> mapSurroundingAreaNames(Set<SurroundingArea> surroundingAreas) {
        return surroundingAreas != null
                ? surroundingAreas.stream().map(SurroundingArea::getName).collect(Collectors.toSet())
                : null;
    }
}
