package vn.edu.iuh.fit.addressservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.addressservice.entity.Province;

public interface ProvinceRepository extends JpaRepository<Province, Long> {
    Province findByCode(String code);
}
