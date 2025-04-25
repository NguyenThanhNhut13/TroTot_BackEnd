/*
 * @ (#) DeductRequest.java       1.0     22/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.model.dto.request;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 22/04/2025
 * @version:    1.0
 */

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeductRequest {
    private Long userId;
    private Long amount;
    private String description;
}
