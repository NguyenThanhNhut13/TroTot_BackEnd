/*
 * @ (#) RoomDetail.java       1.0     14/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.model.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 14/04/2025
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
public class RoomDetail {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "room_id")
    private Room room;

    private int numberOfLivingRooms;
    private int numberOfKitchens;
    private int numberOfBathrooms;
    private int numberOfBedrooms;
}
