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

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.edu.iuh.fit.authservice.client.UserClient;
import vn.edu.iuh.fit.authservice.dto.UserAuthDTO;
import vn.edu.iuh.fit.authservice.dto.UserDTO;
import vn.edu.iuh.fit.authservice.entity.request.LoginRequest;
import vn.edu.iuh.fit.authservice.entity.response.LoginResponse;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        ResponseEntity<UserAuthDTO> response = userClient.getAuthInfo(loginRequest.getCredential());

        if (response == null || response.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        UserAuthDTO userAuth = response.getBody();

        if (!passwordEncoder.matches(loginRequest.getPassword(), userAuth.getHashedPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        // Tạo JWT
        String jwt = jwtService.generateToken(userAuth.getCredential());

        // Lấy thông tin user
        ResponseEntity<UserDTO> userResponse = userClient.getUserInfo(userAuth.getCredential());

        if (userResponse == null || userResponse.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User not found");
        }

        return new LoginResponse(jwt, userResponse.getBody());
    }


}
