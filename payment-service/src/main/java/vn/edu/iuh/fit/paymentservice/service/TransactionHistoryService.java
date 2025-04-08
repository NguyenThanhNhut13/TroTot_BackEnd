package vn.edu.iuh.fit.paymentservice.service;

import vn.edu.iuh.fit.paymentservice.dto.TransactionHistoryDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionHistoryService {
    List<TransactionHistoryDTO> getAll();
    Optional<TransactionHistoryDTO> getById(Long id);
    List<TransactionHistoryDTO> getByUserId(Long userId);
    List<TransactionHistoryDTO> getByDateRange(LocalDate fromDate, LocalDate toDate);
    TransactionHistoryDTO save(TransactionHistoryDTO dto);
    void delete(Long id);
}

