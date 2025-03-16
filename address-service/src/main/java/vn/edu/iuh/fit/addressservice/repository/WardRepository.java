package vn.edu.iuh.fit.addressservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.addressservice.entity.Ward;

public interface WardRepository extends JpaRepository<Ward, Long> {
    Ward findByCode(String code);
}
