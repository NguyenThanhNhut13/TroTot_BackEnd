/*
 * @ (#) AddressSummaryDTO.java       1.0     29/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.model.dto;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 29/04/2025
 * @version:    1.0
 */

import lombok.*;

@Getter
@AllArgsConstructor
public class AddressSummaryDTO {
    private Long id;
    private String province;
    private String district;
}
