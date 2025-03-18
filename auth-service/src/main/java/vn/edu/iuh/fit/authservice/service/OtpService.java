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

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long OTP_EXPIRATION = 2;

    public void sendOtp(String credential) {
        String otp = generateOtp();
//        redisTemplate.opsForValue().set(credential, otp, OTP_EXPIRATION, TimeUnit.MINUTES);
        System.out.println("OTP for " + credential + ": " + otp);
    }

    public boolean verifyOtp(String credential, String otp) {
        String storedOtp = redisTemplate.opsForValue().get(credential);
        return storedOtp != null && storedOtp.equals(otp);
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6 chữ số
    }
}
