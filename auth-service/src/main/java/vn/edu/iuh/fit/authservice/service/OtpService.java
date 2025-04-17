/*
 * @ (#) OtpService.java       1.0     18/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 18/03/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.authservice.enumerate.OtpPurpose;
import vn.edu.iuh.fit.authservice.exception.UnauthorizedException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import vn.edu.iuh.fit.authservice.model.dto.request.OtpEmailRequest;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;
    private static final long OTP_EXPIRATION = 5;
    private static final int MAX_RESEND_PER_DAY = 3;

    private String otpKey(String credential, OtpPurpose purpose) {
        return "OTP:" + purpose.name() + ":" + credential;
    }

    private String resendCountKey(String credential, OtpPurpose purpose) {
        return "OTP:RESEND:" + purpose.name() + ":" + credential;
    }

    public void sendOtp(String credential, OtpPurpose purpose) {
        String resendKey = resendCountKey(credential, purpose);
        String otpStorageKey = otpKey(credential, purpose);

        String currentCountStr = redisTemplate.opsForValue().get(resendKey);
        int resendCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;

        if (resendCount >= MAX_RESEND_PER_DAY) {
            throw new RuntimeException("You have exceeded the OTP resend limit for " + purpose.name().toLowerCase().replace("_", " ") + ".");
        }

        redisTemplate.delete(otpStorageKey);

        String otp = generateOtp();
        redisTemplate.opsForValue().set(otpStorageKey, otp, OTP_EXPIRATION, TimeUnit.MINUTES);
        sendOtpKafka(credential, otp);

        redisTemplate.opsForValue().increment(resendKey);

        // Refresh until midnight
        if (resendCount == 0) {
            redisTemplate.expire(resendKey, getSecondsUntilMidnight(), TimeUnit.SECONDS);
        }

    }

    public void verifyOtp(String credential, String otp, OtpPurpose purpose) {
        String storedOtp = redisTemplate.opsForValue().get(otpKey(credential, purpose));
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new UnauthorizedException("Invalid OTP!");
        }
        deleteOtp(credential, purpose);
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6 chữ số
    }

    public void deleteOtp(String credential, OtpPurpose purpose) {
        redisTemplate.delete(otpKey(credential, purpose));
    }

    private void sendOtpKafka(String email, String otp) {
        try {
            OtpEmailRequest request = new OtpEmailRequest(email, otp);
            String json = objectMapper.writeValueAsString(request);
            kafkaTemplate.send("otp-email", json);
        } catch (Exception e) {
            throw new RuntimeException("Can't send OTP to Kafka");
    }

    private long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, midnight).getSeconds();
    }
}
