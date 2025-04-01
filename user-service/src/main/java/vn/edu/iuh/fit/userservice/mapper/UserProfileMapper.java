/*
 * @ (#) UserProfileMapper.java       1.0     01/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.mapper;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 01/04/2025
 * @version:    1.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.edu.iuh.fit.userservice.model.dto.reponse.UserProfileResponse;
import vn.edu.iuh.fit.userservice.model.entity.UserProfile;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

    UserProfileResponse toDTO(UserProfile userProfile);

    UserProfile toEntity(UserProfileResponse userProfileResponse);
}
