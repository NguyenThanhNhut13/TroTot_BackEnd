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
import vn.edu.iuh.fit.roomservice.enumvalue.GenderType;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomStatus;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private boolean selfManaged;
    private int totalRooms;
    private int maxPeople;

    @Enumerated(EnumType.STRING)
    private GenderType forGender;
    private double deposit;

    // Poster information (if not using default userId)
    private String posterName;
    private String posterPhone;

    private String videoUrl;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Image> images = new HashSet<>();

    @OneToOne(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private RoomDetail roomDetail;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "room_target_audiences",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "target_audience_id")
    )
    private Set<TargetAudience> targetAudiences = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "room_amenities",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "room_surrounding_areas",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "surrounding_area_id")
    )
    private Set<SurroundingArea> surroundingAreas = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public Room(Long id, Long addressId, String title, double price, double area, RoomType roomType) {
        this.id = id;
        this.addressId = addressId;
        this.title = title;
        this.price = price;
        this.area = area;
        this.roomType = roomType;
    }
}
