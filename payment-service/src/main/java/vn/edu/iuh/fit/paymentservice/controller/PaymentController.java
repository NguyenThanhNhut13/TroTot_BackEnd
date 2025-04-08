package vn.edu.iuh.fit.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import vn.edu.iuh.fit.paymentservice.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.paymentservice.dto.DeductRequest;
import vn.edu.iuh.fit.paymentservice.dto.PaymentDTO;
import vn.edu.iuh.fit.paymentservice.dto.BaseResponse;
import vn.edu.iuh.fit.paymentservice.dto.WalletBalanceResponse;
import vn.edu.iuh.fit.paymentservice.service.PaymentService;

@RestController
@RequestMapping("${spring.application.api-prefix}/payment")
@RequiredArgsConstructor
@Tag(name = "Payment Controller", description = "Quản lý thanh toán qua VNPay")
public class PaymentController {

    private final PaymentService paymentService;
    private final WalletService walletService;

    @Operation(
            summary = "Tạo yêu cầu thanh toán VNPay",
            description = "Tạo link thanh toán và redirect người dùng đến trang VNPay",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tạo link thanh toán thành công",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentDTO.VNPayResponse.class)
                            )
                    )
            }
    )

    @Transactional
    @GetMapping("/vn-pay")
    public BaseResponse<PaymentDTO.VNPayResponse> pay(
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        PaymentDTO.VNPayResponse data = paymentService.createVnPayPayment(request);
        return new BaseResponse<>(true, "Tạo link VNPay thành công", data);
    }
    @Operation(
            summary = "Xử lý callback từ VNPay",
            description = "Sau khi người dùng hoàn tất thanh toán trên VNPay, hệ thống sẽ xử lý callback từ VNPay để xác nhận " +
                    "giao dịch thành công. Nếu thành công, tiền sẽ được cộng vào ví người dùng và lưu lịch sử giao dịch.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Thanh toán thành công, tiền đã được cộng vào ví",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentDTO.VNPayResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Giao dịch không thành công hoặc lỗi xử lý",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class)
                            )
                    )
            }
    )

    @Transactional
    @GetMapping("/vn-pay-callback")
    public BaseResponse<PaymentDTO.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        try {
            PaymentDTO.VNPayResponse data = paymentService.handleVNPayCallback(request);
            return new BaseResponse<>(true, "Thanh toán thành công", data);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Thanh toán thất bại: " + e.getMessage(), null);
        }
    }
    @Operation(
            summary = "Trừ tiền ví người dùng",
            description = "API dùng để trừ tiền trong ví của người dùng khi thực hiện các hành động như đăng bài trọ. " +
                    "Nếu người dùng không đủ tiền hoặc chưa có ví, sẽ trả về lỗi.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeductRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Trừ tiền thành công",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Số dư ví không đủ hoặc lỗi đầu vào",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Không tìm thấy ví người dùng",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class)
                            )
                    )
            }
    )

    @Transactional
    @PostMapping("/deduct")
    public BaseResponse<String> deduct(@RequestBody DeductRequest request) {
        return paymentService.deductFromWallet(request);
    }

    @Operation(
            summary = "Lấy số dư ví người dùng",
            description = "API trả về số dư hiện tại của ví theo userId",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lấy số dư thành công",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WalletBalanceResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Không tìm thấy ví người dùng",
                            content = @Content(schema = @Schema(implementation = BaseResponse.class))
                    )
            }
    )
    @GetMapping("/wallet/{userId}")
    public BaseResponse<WalletBalanceResponse> getWalletBalance(
            @Parameter(description = "ID của người dùng") @PathVariable Long userId) {
        WalletBalanceResponse data = walletService.getBalance(userId);
        return new BaseResponse<>(true, "Lấy số dư thành công", data);
    }

}
