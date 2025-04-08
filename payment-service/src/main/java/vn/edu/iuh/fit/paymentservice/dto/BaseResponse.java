package vn.edu.iuh.fit.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
