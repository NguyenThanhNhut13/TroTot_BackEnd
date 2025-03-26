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

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.edu.iuh.fit.userservice.dto.UserDTO;
import vn.edu.iuh.fit.userservice.entity.Role;
import vn.edu.iuh.fit.userservice.entity.User;
import vn.edu.iuh.fit.userservice.service.RoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserDTO toDTO(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "roles", expression = "java(mapRolesFromDTO(userDTO.getRoles()))")
    User toEntity(UserDTO userDTO);

    default List<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }

    default Set<Role> mapRolesFromDTO(List<String> roles) {
        return null;
    }

}
