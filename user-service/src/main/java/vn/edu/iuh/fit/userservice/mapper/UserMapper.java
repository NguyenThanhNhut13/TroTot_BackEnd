/*
 * @ (#) UserMapper.java       1.0     09/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.mapper;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 09/03/2025
 * @version:    1.0
 */

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.edu.iuh.fit.userservice.dto.UserDTO;
import vn.edu.iuh.fit.userservice.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", source = "id")
    UserDTO toDTO(User user);

    @Mapping(target = "id", source = "id")
    User toEntity(UserDTO userDTO);

}
