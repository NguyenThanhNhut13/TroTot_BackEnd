package vn.edu.iuh.fit.addressservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.addressservice.entity.Ward;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, String> {
    List<Ward> findByDistrict_Code(String districtCode);
}
