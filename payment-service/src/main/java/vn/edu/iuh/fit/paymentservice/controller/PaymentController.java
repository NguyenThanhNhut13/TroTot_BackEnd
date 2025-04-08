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
import vn.edu.iuh.fit.paymentservice.response.ResponseObject;
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
    public ResponseObject<PaymentDTO.VNPayResponse> pay(
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        return new ResponseObject<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request));
    }

    @Operation(
            summary = "Xử lý callback từ VNPay",
            description = "Xử lý kết quả thanh toán sau khi người dùng hoàn tất tại VNPay và được chuyển hướng về",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Thanh toán thành công"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Thanh toán thất bại"
                    )
            }
    )
    @GetMapping("/vn-pay-callback")
    public ResponseObject<PaymentDTO.VNPayResponse> payCallbackHandler(
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            return new ResponseObject<>(HttpStatus.OK, "Success", new PaymentDTO.VNPayResponse("00", "Success", ""));
        } else {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST, "Failed", null);
        }
    }
}
