/*
 * @ (#) TargetAudience.java       1.0     14/04/2025
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

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetAudience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "targetAudiences")
    private Set<Room> rooms = new HashSet<>();
}
