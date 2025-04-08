package vn.edu.iuh.fit.paymentservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.iuh.fit.paymentservice.model.TransactionHistory;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findByUserId(Long userId);
}
