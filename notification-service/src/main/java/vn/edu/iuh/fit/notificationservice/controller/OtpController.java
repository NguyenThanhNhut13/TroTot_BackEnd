package vn.edu.iuh.fit.notificationservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.iuh.fit.notificationservice.service.EmailService;
import vn.edu.iuh.fit.notificationservice.service.OtpService;
import vn.edu.iuh.fit.notificationservice.util.BaseResponse;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;
    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<BaseResponse<String>> sendOtp(@RequestParam String email) {
        try {
            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok(BaseResponse.ok("OTP đã được gửi tới " + email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(BaseResponse.error("Gửi OTP thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<String>> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok(BaseResponse.ok("Xác thực OTP Thành công"));
        } else {
            return ResponseEntity.badRequest().body(BaseResponse.error("OTP không đúng hoặc đã hết hạn !"));
        }
    }
}
