/*
 * @ (#) ErrorResponse.java       1.0     16/05/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.addressservice.dto.response;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 16/05/2025
 * @version:    1.0
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
}
