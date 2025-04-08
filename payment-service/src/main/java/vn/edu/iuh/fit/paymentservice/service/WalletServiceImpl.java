package vn.edu.iuh.fit.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.paymentservice.dto.WalletBalanceResponse;
import vn.edu.iuh.fit.paymentservice.model.Wallet;
import vn.edu.iuh.fit.paymentservice.repository.WalletRepository;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public WalletBalanceResponse getBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ví của người dùng"));
        return new WalletBalanceResponse(wallet.getUserId(), wallet.getBalance());
    }
}
