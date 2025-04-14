/*
 * @ (#) TargetAudienceMapper.java       1.0     11/04/2025
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
import vn.edu.iuh.fit.roomservice.model.dto.TargetAudienceDTO;
import vn.edu.iuh.fit.roomservice.model.entity.SurroundingArea;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TargetAudienceMapper {
    TargetAudienceDTO toDTO(SurroundingArea targetAudience);
    SurroundingArea toEntity(TargetAudienceDTO dto);
    List<TargetAudienceDTO> toDTOs(List<SurroundingArea> list);
    List<SurroundingArea> toEntities(List<TargetAudienceDTO> list);
}

