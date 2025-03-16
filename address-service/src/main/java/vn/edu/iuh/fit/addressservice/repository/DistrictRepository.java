package vn.edu.iuh.fit.addressservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.addressservice.entity.District;

public interface DistrictRepository extends JpaRepository<District, Long> {
    District findByCode(String code);
}
