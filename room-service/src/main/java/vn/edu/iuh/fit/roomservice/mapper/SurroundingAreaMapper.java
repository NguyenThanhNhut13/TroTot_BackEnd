/*
 * @ (#) EnvironmentMapper.java       1.0     11/04/2025
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
import vn.edu.iuh.fit.roomservice.model.dto.SurroundingAreaDTO;
import vn.edu.iuh.fit.roomservice.model.entity.SurroundingArea;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SurroundingAreaMapper {
    SurroundingAreaDTO toDTO(SurroundingArea surroundingArea);
    SurroundingArea toEntity(SurroundingAreaDTO dto);
    List<SurroundingAreaDTO> toDTOs(List<SurroundingArea> list);
    List<SurroundingArea> toEntities(List<SurroundingAreaDTO> list);
}
