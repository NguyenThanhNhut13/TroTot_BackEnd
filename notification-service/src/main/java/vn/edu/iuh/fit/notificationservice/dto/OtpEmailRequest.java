package vn.edu.iuh.fit.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
public class OtpEmailRequest {
    private String toEmail;
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public OtpEmailRequest(String otp, String toEmail) {
        this.otp = otp;
        this.toEmail = toEmail;
    }

    public OtpEmailRequest() {
    }
}
