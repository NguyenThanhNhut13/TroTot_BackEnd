package vn.edu.iuh.fit.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeductRequest {
    private Long userId;
    private Long amount;
    private String description;
}

