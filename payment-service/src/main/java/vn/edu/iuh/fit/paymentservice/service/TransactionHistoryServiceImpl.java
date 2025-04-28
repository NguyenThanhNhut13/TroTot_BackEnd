package vn.edu.iuh.fit.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.paymentservice.dto.TransactionHistoryDTO;
import vn.edu.iuh.fit.paymentservice.model.TransactionHistory;
import vn.edu.iuh.fit.paymentservice.repository.TransactionHistoryRepository;
import vn.edu.iuh.fit.paymentservice.service.TransactionHistoryService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionHistoryServiceImpl implements TransactionHistoryService {

    private final TransactionHistoryRepository repository;

    private TransactionHistoryDTO toDTO(TransactionHistory entity) {
        TransactionHistoryDTO dto = new TransactionHistoryDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setAmount(entity.getAmount());
        dto.setTransactionType(entity.getTransactionType());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    private TransactionHistory toEntity(TransactionHistoryDTO dto) {
        TransactionHistory entity = new TransactionHistory();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setAmount(dto.getAmount());
        entity.setTransactionType(dto.getTransactionType());
        entity.setDescription(dto.getDescription());
        entity.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        return entity;
    }

    @Override
    public List<TransactionHistoryDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TransactionHistoryDTO> getById(Long id) {
        return repository.findById(id).map(this::toDTO);
    }

    @Override
    public List<TransactionHistoryDTO> getByUserId(Long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionHistoryDTO> getByDateRange(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.plusDays(1).atStartOfDay().minusNanos(1); // till end of "toDate"
        return repository.findAll().stream()
                .filter(trx -> trx.getCreatedAt() != null
                        && (trx.getCreatedAt().isEqual(from) || trx.getCreatedAt().isAfter(from))
                        && (trx.getCreatedAt().isBefore(to) || trx.getCreatedAt().isEqual(to)))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionHistoryDTO save(TransactionHistoryDTO dto) {
        TransactionHistory saved = repository.save(toEntity(dto));
        return toDTO(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
