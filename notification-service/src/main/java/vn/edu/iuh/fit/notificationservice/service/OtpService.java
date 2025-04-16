package vn.edu.iuh.fit.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class OtpService {

    private static final int OTP_EXPIRATION_MINUTES = 5;
    private final ConcurrentHashMap<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateOtp(String email) {
        String otp = String.format("%06d", random.nextInt(999999));
        otpStore.put(email, new OtpEntry(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRATION_MINUTES)));
        return otp;
    }

    public boolean verifyOtp(String email, String otpInput) {
        OtpEntry entry = otpStore.get(email);
        if (entry == null) return false;
        if (entry.getExpiryTime().isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return false;
        }
        boolean isValid = entry.getOtp().equals(otpInput);
        if (isValid) otpStore.remove(email);
        return isValid;
    }

    private record OtpEntry(String otp, LocalDateTime expiryTime) {
        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpiryTime() {
            return expiryTime;
        }
    }
}
