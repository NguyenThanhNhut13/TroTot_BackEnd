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
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.userservice.model.dto.request.RegisterRequest;
import vn.edu.iuh.fit.userservice.exception.UserAlreadyExistsException;
import vn.edu.iuh.fit.userservice.model.entity.UserProfile;
import vn.edu.iuh.fit.userservice.repository.UserProfileRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserProfileRepository userRepository;

    public void saveUser(UserProfile user) {
        userRepository.save(user);
    }

    public void createUser(RegisterRequest request) {
        if (userRepository.existsUserProfilesById(request.getId())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        UserProfile userProfile = new UserProfile();

        userProfile.setId(request.getId());
        userProfile.setFullName(request.getFullName());
        userRepository.save(userProfile);
    }


}
