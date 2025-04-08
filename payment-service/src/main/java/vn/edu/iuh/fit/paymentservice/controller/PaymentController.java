package vn.edu.iuh.fit.paymentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.iuh.fit.paymentservice.dto.PaymentDTO;
import vn.edu.iuh.fit.paymentservice.model.BaseResponse;
import vn.edu.iuh.fit.paymentservice.service.PaymentService;

@RestController
@RequestMapping("${spring.application.api-prefix}/payment")
@RequiredArgsConstructor
@Tag(name = "Payment Controller", description = "Quản lý thanh toán qua VNPay")
public class PaymentController {

    private final PaymentService paymentService;

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
    @GetMapping("/vn-pay")
    public BaseResponse<PaymentDTO.VNPayResponse> pay(
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        PaymentDTO.VNPayResponse data = paymentService.createVnPayPayment(request);
        return new BaseResponse<>(true, "Tạo link VNPay thành công", data);
    }

    @GetMapping("/vn-pay-callback")
    public BaseResponse<PaymentDTO.VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        try {
            PaymentDTO.VNPayResponse data = paymentService.handleVNPayCallback(request);
            return new BaseResponse<>(true, "Thanh toán thành công", data);
        } catch (Exception e) {
            return new BaseResponse<>(false, "Thanh toán thất bại: " + e.getMessage(), null);
        }
    }
}
