package vn.edu.iuh.fit.roomservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.roomservice.model.entity.Amenity;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
}