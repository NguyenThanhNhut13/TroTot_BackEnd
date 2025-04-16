package vn.edu.iuh.fit.roomservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.roomservice.model.entity.TargetAudience;

public interface TargetAudienceRepository extends JpaRepository<TargetAudience, Long> {
}