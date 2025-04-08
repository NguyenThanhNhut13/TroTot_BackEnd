package vn.edu.iuh.fit.paymentservice.service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.paymentservice.config.VNPAYConfig;
import vn.edu.iuh.fit.paymentservice.dto.BaseResponse;
import vn.edu.iuh.fit.paymentservice.dto.DeductRequest;
import vn.edu.iuh.fit.paymentservice.dto.PaymentDTO;
import vn.edu.iuh.fit.paymentservice.exception.custom.CustomException;
import vn.edu.iuh.fit.paymentservice.model.TransactionHistory;
import vn.edu.iuh.fit.paymentservice.model.TransactionType;
import vn.edu.iuh.fit.paymentservice.model.Wallet;
import vn.edu.iuh.fit.paymentservice.repository.TransactionHistoryRepository;
import vn.edu.iuh.fit.paymentservice.repository.WalletRepository;
import vn.edu.iuh.fit.paymentservice.util.VNPayUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    private final WalletRepository walletRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        String userId = request.getParameter("userId");

        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));

        // 👇 Add userId to vnp_TxnRef
        String txnRef = userId + "-" + VNPayUtil.getRandomNumber(6);
        vnpParamsMap.put("vnp_TxnRef", txnRef);

        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }

        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;

        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }


    public PaymentDTO.VNPayResponse handleVNPayCallback(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef"); // ví dụ "1-123456"
        String amountStr = request.getParameter("vnp_Amount");

        if (!"00".equals(responseCode) || txnRef == null || amountStr == null) {
            throw new RuntimeException("Invalid callback params");
        }

        String userIdStr = txnRef.split("-")[0]; // tách userId từ TxnRef
        Long userId = Long.parseLong(userIdStr);
        long amountVND = Long.parseLong(amountStr) / 100L;

        //  Kiểm tra ví, tạo mới nếu chưa có
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setUserId(userId);
                    newWallet.setBalance(BigDecimal.ZERO);
                    return newWallet;
                });

        //  Cộng tiền
        wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amountVND)));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        //  Ghi log giao dịch
        TransactionHistory transaction = new TransactionHistory();
        transaction.setUserId(userId);
        transaction.setAmount(BigDecimal.valueOf(amountVND));
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setDescription("Nạp tiền qua VNPay");
        transactionHistoryRepository.save(transaction);

        return PaymentDTO.VNPayResponse.builder()
                .code("00")
                .message("Nạp tiền thành công")
                .paymentUrl("")
                .build();
    }

    public BaseResponse<String> deductFromWallet(DeductRequest request) {
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Ví chưa được tạo"));

        BigDecimal amount = BigDecimal.valueOf(request.getAmount());
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Số dư ví không đủ");
        }

        // Trừ tiền
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        // Ghi log
        TransactionHistory transaction = new TransactionHistory();
        transaction.setUserId(request.getUserId());
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.PURCHASE);
        transaction.setDescription("Trừ tiền khi đăng bài trọ"); // mặc định, không cần client truyền
        transactionHistoryRepository.save(transaction);

        return new BaseResponse<>(true, "Trừ tiền thành công", null);
    }


}
