/*
 * @ (#) RoomDTO.java       1.0     21/02/2025
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
import vn.edu.iuh.fit.roomservice.enumvalue.RoomStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long id;
    private Long userId;
    private AddressDTO address;
    private String title;
    private String description;
    private double price;
    private double area;
    private List<ImageDTO> images;
    private RoomStatus status;
    private List<AmenityDTO> amenities;
    private List<SurroundingAreaDTO> environments;
    private List<TargetAudienceDTO> targetAudiences;

    // From RoomDetail
    private Integer numberOfLivingRooms;
    private Integer numberOfKitchens;
    private Integer numberOfBathrooms;
    private Integer numberOfBedrooms;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
