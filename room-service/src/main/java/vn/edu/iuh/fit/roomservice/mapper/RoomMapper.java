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

import org.mapstruct.Mapper;
import vn.edu.iuh.fit.roomservice.model.dto.RoomDTO;
import vn.edu.iuh.fit.roomservice.model.entity.Room;

@Mapper(componentModel = "spring", uses = {
        AmenityMapper.class,
        EnvironmentMapper.class,
        TargetAudienceMapper.class,
        ImageMapper.class
})
public interface RoomMapper {
    RoomDTO toDTO(Room room);
    Room toEntity(RoomDTO roomDTO);
}
