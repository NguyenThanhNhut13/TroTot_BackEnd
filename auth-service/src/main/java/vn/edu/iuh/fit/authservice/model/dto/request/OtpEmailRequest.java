package vn.edu.iuh.fit.authservice.model.dto.request;

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
