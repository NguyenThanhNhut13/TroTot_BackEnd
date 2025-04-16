/*
 * @ (#) RoomDetailRepository.java       1.0     14/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.repository;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 14/04/2025
 * @version:    1.0
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.roomservice.model.entity.RoomDetail;

@Repository
public interface RoomDetailRepository extends JpaRepository<RoomDetail, Long> {
}
