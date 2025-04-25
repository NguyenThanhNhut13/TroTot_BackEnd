/*
 * @ (#) RoomTrainDTO.java       1.0     21/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.model.dto;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 21/04/2025
 * @version:    1.0
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.edu.iuh.fit.roomservice.enumvalue.GenderType;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomTrainDTO {
    private Long id;
    private String title;
    private String description;
    private double price;
    private double area;
    private boolean selfManaged;
    private GenderType forGender;
    private RoomType roomType;
    private Set<String> amenities;
    private Set<String> targetAudiences;
    private Set<String> surroundingAreas;

    private String province;
    private String district;
}

