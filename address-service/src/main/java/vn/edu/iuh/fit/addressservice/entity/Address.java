/*
 * @ (#) Address.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.addressservice.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 11/02/2025
 * @version:    1.0
 */

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;
    private String province;
    private String district;
    private String ward;
    private String street;
    private String houseNumber;

    private Double latitude;
    private Double longitude;

}
