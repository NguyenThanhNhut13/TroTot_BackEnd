/*
 * @ (#) RoleRepository.java       1.0     21/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.repository;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 21/03/2025
 * @version:    1.0
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.authservice.model.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String name);
    boolean existsByRoleName(String roleName);
}
