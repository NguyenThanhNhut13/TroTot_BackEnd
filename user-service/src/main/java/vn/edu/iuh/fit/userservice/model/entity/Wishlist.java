/*
 * @ (#) Wishlist.java       1.0     22/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.model.entity;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 22/04/2025
 * @version:    1.0
 */

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @CreationTimestamp
    private LocalDateTime savedAt;
}

