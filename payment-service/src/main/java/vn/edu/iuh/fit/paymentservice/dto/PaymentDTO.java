package vn.edu.iuh.fit.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public abstract class PaymentDTO {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class VNPayResponse {
        public String code;
        public String message;
        public String paymentUrl;
    }
}
