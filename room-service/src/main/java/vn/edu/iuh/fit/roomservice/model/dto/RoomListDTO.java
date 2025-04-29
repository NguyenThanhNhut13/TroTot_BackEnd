/*
 * @ (#) RoomListDTO.java       1.0     20/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.model.dto;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 20/04/2025
 * @version:    1.0
 */

import lombok.*;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomListDTO {
    private Long id;
    private String title;
    private double price;
    private double area;
    private RoomType roomType;
    private List<String> imageUrls;
    private String district;
    private String province;

}
