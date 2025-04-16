package vn.edu.iuh.fit.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpEmailRequest {
    private String toEmail;
    private String otp;
}
