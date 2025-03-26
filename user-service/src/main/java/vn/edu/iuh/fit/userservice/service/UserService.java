/*
 * @ (#) UserService.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.userservice.dto.UserAuthDTO;
import vn.edu.iuh.fit.userservice.dto.UserDTO;
import vn.edu.iuh.fit.userservice.entity.Permission;
import vn.edu.iuh.fit.userservice.entity.Role;
import vn.edu.iuh.fit.userservice.entity.User;
import vn.edu.iuh.fit.userservice.entity.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.exception.UserAlreadyExistsException;
import vn.edu.iuh.fit.userservice.mapper.UserMapper;
import vn.edu.iuh.fit.userservice.repository.PermissionRepository;
import vn.edu.iuh.fit.userservice.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PermissionRepository permissionRepository;
    private final RoleService roleService;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public UserDTO findByEmailOrPhone(String credential) {
        return userMapper.toDTO(userRepository.findUserByEmailOrPhoneNumber(credential));
    }

    public UserAuthDTO getAuthInfoByCredential(String credential) {
        User user = userRepository.findUserByEmailOrPhoneNumber(credential);
        if (user == null) {
            return null;
        }
        return new UserAuthDTO(user.getEmail(), user.getPassword());
    }

    public UserDTO getUserByCredential(String credential) {
        User user = userRepository.findUserByEmailOrPhoneNumber(credential);
        if (user == null) {
            return null;
        }

        if (!user.isVerified()) {
            return null;
        }

        return userMapper.toDTO(user);
    }

    public void createUser(RegisterRequest request) {
        if (userRepository.existsByEmailOrPhoneNumber(request.getCredential())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = new User();
        if (request.getCredential().contains("@")) {
            user.setEmail(request.getCredential());
        } else {
            user.setPhoneNumber(request.getCredential());
        }
        user.setFullName(request.getFullName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setVerified(false);
        userRepository.save(user);
    }

    public boolean hasPermission(String url, String method, List<String> roles) {
        List<Permission> permissions = permissionRepository.findPermissionsByRoles(roles);
        return permissions.stream()
                .anyMatch(p -> p.getUrl().equals(url) && p.getMethod().equalsIgnoreCase(method));
    }

    public UserDTO getUserDTOById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Chuyển danh sách role name thành Set<Role> tại đây
        List<String> roleNames = userMapper.mapRoles(user.getRoles());
        Set<Role> roles = roleNames.stream()
                .map(roleService::getRoleByName)  // Lấy Role từ tên
                .collect(Collectors.toSet());

        // Tạo UserDTO từ User và cung cấp roles
        UserDTO userDTO = userMapper.toDTO(user);
        userDTO.setRoles(roleNames);  // Set lại roles ở đây hoặc trực tiếp chuyển roles vào DTO.

        return userDTO;
    }
}
