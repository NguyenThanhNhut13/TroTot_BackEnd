/*
 * @ (#) TargetAudience.java       1.0     21/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.model.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 21/02/2025
 * @version:    1.0
 */

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurroundingArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "surroundingAreas")
    private List<Room> rooms;
}
