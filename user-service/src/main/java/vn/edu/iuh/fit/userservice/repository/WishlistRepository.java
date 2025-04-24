/*
 * @ (#) WishlistRepository.java       1.0     22/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.repository;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 22/04/2025
 * @version:    1.0
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.userservice.model.entity.Wishlist;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    List<Wishlist> findByUserProfileId(Long userId);
    boolean existsByUserProfileIdAndRoomId(Long userId, Long roomId);
    Optional<Wishlist> findByUserProfileIdAndRoomId(Long userId, Long roomId);

    @Query("SELECT w.userProfile.id, w.roomId FROM Wishlist w")
    List<Object[]> findAllUserWishlistRaw();
}
