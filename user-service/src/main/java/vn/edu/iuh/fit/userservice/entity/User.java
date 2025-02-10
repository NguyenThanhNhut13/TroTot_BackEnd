/*
 * @ (#) User.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import jakarta.persistence.*;
import lombok.*;
import vn.edu.iuh.fit.userservice.enumvalue.UserRole;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    @Column(unique = true, length = 11)
    private String phoneNumber;
    private String password;

    private String address;
    private LocalDateTime dob;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
