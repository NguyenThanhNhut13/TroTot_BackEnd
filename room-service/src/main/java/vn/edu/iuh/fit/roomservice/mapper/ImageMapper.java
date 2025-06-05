/*
 * @ (#) ImageMapper.java       1.0     11/04/2025
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
import org.mapstruct.Mapping;
import vn.edu.iuh.fit.roomservice.model.dto.ImageDTO;
import vn.edu.iuh.fit.roomservice.model.entity.Image;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageDTO toDTO(Image roomImage);
    @Mapping(target = "id", ignore = true)
    Image toEntity(ImageDTO roomImageDTO);
    List<ImageDTO> toDTOs(List<Image> images);
    List<Image> toEntities(List<ImageDTO> imageDTOs);
}
