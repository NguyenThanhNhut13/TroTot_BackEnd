/*
 * @ (#) UpdateUserProfileRequest.java       1.0     20/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.model.dto.request;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 20/04/2025
 * @version:    1.0
 */

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.fit.userservice.enumeraion.Gender;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UpdateUserProfileRequest {
    private String fullName;
    private LocalDateTime dob;
    private String address;
    private String cccd;
    private Gender gender;
}
