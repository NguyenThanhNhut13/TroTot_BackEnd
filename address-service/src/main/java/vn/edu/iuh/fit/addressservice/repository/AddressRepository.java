/*
 * @ (#) AddressRepository.java       1.0     10/02/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.addressservice.repository;
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
import vn.edu.iuh.fit.addressservice.dto.AddressDTO;
import vn.edu.iuh.fit.addressservice.entity.Address;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE " +
            "(:street IS NULL OR a.street LIKE %:street%) AND " +
            "(:district IS NULL OR a.district LIKE %:district%) AND " +
            "(:province IS NULL OR a.province LIKE %:province%)")
    List<Address> findByProvinceLikeAndDistrictLikeAndStreetLike(@Param("street") String street,
                                                                    @Param("district") String district,
                                                                    @Param("province") String province);

}
