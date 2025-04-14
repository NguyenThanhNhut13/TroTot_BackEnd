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

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import vn.edu.iuh.fit.roomservice.enumvalue.GenderType;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomStatus;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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
    private boolean isSelfManaged;
    private int totalRooms;
    private int maxPeople;
    private GenderType forGender;
    private double deposit;

    // Poster information (if userId is null)
    private String posterName;
    private String posterPhone;

    private List<ImageDTO> images;
    private RoomType roomType;
    private Set<AmenityDTO> amenities;
    private Set<SurroundingAreaDTO> surroundingAreas;
    private Set<TargetAudienceDTO> targetAudiences;

    // From RoomDetail
    private Integer numberOfLivingRooms;
    private Integer numberOfKitchens;
    private Integer numberOfBathrooms;
    private Integer numberOfBedrooms;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
