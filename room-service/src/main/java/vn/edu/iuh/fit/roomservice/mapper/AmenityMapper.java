/*
 * @ (#) AmenityMapper.java       1.0     11/04/2025
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
import vn.edu.iuh.fit.roomservice.model.dto.AmenityDTO;
import vn.edu.iuh.fit.roomservice.model.entity.Amenity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AmenityMapper {
    AmenityDTO toDTO(Amenity amenity);
    Amenity toEntity(AmenityDTO dto);
    List<AmenityDTO> toDTOs(List<Amenity> list);
    List<Amenity> toEntities(List<AmenityDTO> list);
}

