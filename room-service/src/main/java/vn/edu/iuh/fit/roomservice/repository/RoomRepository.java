/*
 * @ (#) UserRepository.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.repository;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.roomservice.enumvalue.RoomType;
import vn.edu.iuh.fit.roomservice.model.dto.RoomListDTO;
import vn.edu.iuh.fit.roomservice.model.entity.Room;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    List<Room> findByAddressIdIn(List<Long> addressIds);


    @Query("""
        SELECT new vn.edu.iuh.fit.roomservice.model.entity.Room(
            r.id, r.addressId, r.title, r.price, r.area, r.roomType
        )
        FROM Room r
    """)
    Page<Room> findAllRoom(Pageable pageable);

    @Query("""
        SELECT new vn.edu.iuh.fit.roomservice.model.entity.Room(
            r.id, r.addressId, r.title, r.price, r.area, r.roomType
        )
        FROM Room r
        WHERE r.roomType = :roomType
    """)
    Page<Room> findByRoomType(@Param("roomType") RoomType roomType, Pageable pageable);


    @Query("""
        SELECT new vn.edu.iuh.fit.roomservice.model.entity.Room(
            r.id, r.addressId, r.title, r.price, r.area, r.roomType
        )
        FROM Room r
        WHERE r.id IN :ids
    """)
    List<Room> findByIds(@Param("ids") List<Long> ids);

    @Query("""
        SELECT new vn.edu.iuh.fit.roomservice.model.entity.Room(
            r.id, r.addressId, r.title, r.price, r.area, r.roomType
        )
        FROM Room r
        WHERE r.userId = :userId
   """)
    Page<Room> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
