package vn.edu.iuh.fit.paymentservice.service;

import vn.edu.iuh.fit.paymentservice.dto.WalletBalanceResponse;

public interface WalletService {
    WalletBalanceResponse getBalance(Long userId);
}
