/*
 * @ (#) User.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import jakarta.persistence.*;
import lombok.*;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomAmenity;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long addressId;
    private String title;
    private String description;
    private double price;
    private double area;

    @ElementCollection
    private List<String> images;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<RoomAmenity> amenities;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
