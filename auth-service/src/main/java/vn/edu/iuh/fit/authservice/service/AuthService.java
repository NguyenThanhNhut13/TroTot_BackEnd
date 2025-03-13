/*
 * @ (#) AuthService.java       1.0     13/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 13/03/2025
 * @version:    1.0
 */

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.authservice.dto.UserAuthDTO;
import vn.edu.iuh.fit.authservice.entity.request.LoginRequest;

@Service
public class AuthService {

    public boolean authenticate(LoginRequest loginRequest, UserAuthDTO user) {
        return BCrypt.checkpw(loginRequest.getPassword(), user.getHashedPassword());
    }
}
