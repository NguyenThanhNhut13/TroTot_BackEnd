/*
 * @ (#) UserAuthDTO.java       1.0     13/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.dto;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 13/03/2025
 * @version:    1.0
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDTO {
    private String credential;
    private String hashedPassword;
}
