package vn.edu.iuh.fit.notificationservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom("olachatservice@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("🔐 Mã Xác Thực OTP - Trọ Tốt");

            String html = "<!DOCTYPE html>" +
                    "<html lang=\"vi\">" +
                    "<head><meta charset=\"UTF-8\"><title>Xác thực OTP - Trọ Tốt</title></head>" +
                    "<body style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">" +
                    "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);\">" +
                    "<h2 style=\"color: #2E86C1; text-align: center;\">🔐 Xác Thực Tài Khoản</h2>" +
                    "<p style=\"font-size: 16px;\">Xin chào,</p>" +
                    "<p style=\"font-size: 16px;\">Bạn vừa yêu cầu xác thực tài khoản tại <strong>Trọ Tốt</strong>.</p>" +
                    "<p style=\"font-size: 16px;\">Vui lòng sử dụng mã OTP bên dưới để hoàn tất quá trình xác thực:</p>" +
                    "<div style=\"text-align: center; margin: 30px 0;\">" +
                    "<span style=\"font-size: 28px; font-weight: bold; letter-spacing: 4px; background-color: #f1f1f1; padding: 15px 25px; border-radius: 8px; display: inline-block; color: #e74c3c;\">" +
                    otp +
                    "</span></div>" +
                    "<p style=\"font-size: 15px;\">Mã này có hiệu lực trong vòng <strong>5 phút</strong>. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>" +
                    "<p style=\"font-size: 14px; color: #888;\">Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email này hoặc liên hệ với bộ phận hỗ trợ của chúng tôi.</p>" +
                    "<hr style=\"margin: 30px 0;\">" +
                    "<p style=\"font-size: 13px; text-align: center; color: #999;\">© 2025 Trọ Tốt - Kết nối chỗ trọ an toàn và nhanh chóng</p>" +
                    "</div></body></html>";

            helper.setText(html, true);

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Không thể gửi email: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }
}
