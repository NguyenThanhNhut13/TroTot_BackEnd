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
import vn.edu.iuh.fit.roomservice.model.dto.EnvironmentDTO;
import vn.edu.iuh.fit.roomservice.model.entity.Environment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnvironmentMapper {
    EnvironmentDTO toDTO(Environment environment);
    Environment toEntity(EnvironmentDTO dto);
    List<EnvironmentDTO> toDTOs(List<Environment> list);
    List<Environment> toEntities(List<EnvironmentDTO> list);
}
