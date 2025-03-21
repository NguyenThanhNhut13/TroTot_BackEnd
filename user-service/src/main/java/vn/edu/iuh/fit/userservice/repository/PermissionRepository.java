/*
 * @ (#) PermissionRepository.java       1.0     21/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.userservice.repository;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 21/03/2025
 * @version:    1.0
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.userservice.entity.Permission;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    @Query("SELECT p FROM Permission p JOIN p.roles r WHERE r.roleName IN :roles")
    List<Permission> findPermissionsByRoles(@Param("roles") List<String> roles);
}