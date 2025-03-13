/*
 * @ (#) JwtResponse.java       1.0     09/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.entity.response;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 09/03/2025
 * @version:    1.0
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import vn.edu.iuh.fit.authservice.dto.UserDTO;

@Data
@AllArgsConstructor
public class LoginResponse {
    private final String jwt;
    private UserDTO user;
}
