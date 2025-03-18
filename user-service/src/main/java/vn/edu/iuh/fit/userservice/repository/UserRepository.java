/*
 * @ (#) UserRepository.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.repository;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 10/02/2025
 * @version:    1.0
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.userservice.dto.UserDTO;
import vn.edu.iuh.fit.userservice.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :credential OR u.phoneNumber = :credential")
    User findUserByEmailOrPhoneNumber(@Param("credential") String credential);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :credential OR u.phoneNumber = :credential")
    boolean existsByEmailOrPhoneNumber(@Param("credential") String credential);
}
