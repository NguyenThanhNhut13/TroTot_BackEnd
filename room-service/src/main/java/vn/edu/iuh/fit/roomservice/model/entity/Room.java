/*
 * @ (#) User.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.model.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import jakarta.persistence.*;
import lombok.*;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomStatus;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;

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

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @ManyToMany
    @JoinTable(
            name = "room_amenity",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;

    @ManyToMany
    @JoinTable(
            name = "room_environment",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "environment_id")
    )
    private List<Environment> environments;

    @ManyToMany
    @JoinTable(
            name = "room_target_audience",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "target_audience_id")
    )
    private List<TargetAudience> targetAudiences;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
