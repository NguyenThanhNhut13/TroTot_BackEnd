/*
 * @ (#) AddressDTO.java       1.0     21/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.model.dto;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 21/02/2025
 * @version:    1.0
 */

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AddressDTO {
    private Long id;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String houseNumber;
    private Double latitude;
    private Double longitude;
}
