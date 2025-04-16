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
import vn.edu.iuh.fit.authservice.exception.UnauthorizedException;

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
    private final String key = "OTP: ";

    public void sendOtp(String credential) {
        String otp = generateOtp();
        redisTemplate.opsForValue().set(key + credential, otp, OTP_EXPIRATION, TimeUnit.MINUTES);
        sendOtpKafka(credential, otp); // gửi Kafka
        System.out.println("OTP for " + credential + ": " + otp);
    }


    public void verifyOtp(String credential, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(key + credential);
        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new UnauthorizedException("Invalid OTP!");
        }
        deleteOtp(credential);
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6 chữ số
    }

    public void deleteOtp(String credential) {
        redisTemplate.delete(key + credential);
    }

    public void forgotPassword(String credential) {
        String otp = generateOtp();
        redisTemplate.opsForValue().set(key + credential, otp, OTP_EXPIRATION, TimeUnit.MINUTES);
        sendOtpKafka(credential, otp); // gửi Kafka
        System.out.println("OTP for " + credential + ": " + otp);
    }

    private void sendOtpKafka(String email, String otp) {
        try {
            OtpEmailRequest request = new OtpEmailRequest(email, otp);
            String json = objectMapper.writeValueAsString(request);
            kafkaTemplate.send("otp-email", json);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi OTP qua Kafka");
        }
    }
}
