/*
 * @ (#) UserResponse.java       1.0     01/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.model.dto.response;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 01/04/2025
 * @version:    1.0
 */

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String phoneNumber;
    private List<String> roles;
    private boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
