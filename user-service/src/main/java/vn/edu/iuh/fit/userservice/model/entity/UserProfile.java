/*
 * @ (#) UserProfile.java       1.0     29/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.model.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 29/03/2025
 * @version:    1.0
 */

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private Long id;

    private String fullName;

    private String address;

    private LocalDateTime dob;

    @Column(nullable = false)
    private Integer numberOfPosts = 1;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
