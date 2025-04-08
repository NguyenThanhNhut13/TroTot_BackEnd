package vn.edu.iuh.fit.paymentservice.dto;

import lombok.Data;
import vn.edu.iuh.fit.paymentservice.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionHistoryDTO {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String description;
    private LocalDateTime createdAt;
}
