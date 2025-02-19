/*
 * @ (#) Amenity.java       1.0     19/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 19/02/2025
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
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

}
