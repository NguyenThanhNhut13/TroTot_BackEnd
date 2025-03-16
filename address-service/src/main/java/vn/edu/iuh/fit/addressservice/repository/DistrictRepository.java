package vn.edu.iuh.fit.addressservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.addressservice.entity.District;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, String> {
    List<District> findByProvince_Code(String provinceCode);
}
