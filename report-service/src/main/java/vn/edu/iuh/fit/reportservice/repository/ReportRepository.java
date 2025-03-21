package vn.edu.iuh.fit.reportservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.reportservice.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}