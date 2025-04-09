/*
 * @ (#) TokenRedisService.java       1.0     09/04/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.service;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 09/04/2025
 * @version:    1.0
 */

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenRedisService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String BLACKLIST_ACCESS_TOKEN_PREFIX = "blacklist:access_token:";

    // Save refresh token with TTL
    public void saveRefreshToken(String tokenId, String tokenValue, long ttl, TimeUnit unit) {
        String key = REFRESH_TOKEN_PREFIX + tokenId;
        redisTemplate.opsForValue().set(key, tokenValue);
        redisTemplate.expire(key, ttl, unit);
    }

    // Delete refresh token (when logout)
    public void deleteRefreshToken(String tokenId) {
        String key = REFRESH_TOKEN_PREFIX + tokenId;
        redisTemplate.delete(key);
    }

    // Save access token into blacklist with TTL
    public void blacklistAccessToken(String tokenId, long ttl, TimeUnit unit) {
        String key = BLACKLIST_ACCESS_TOKEN_PREFIX + tokenId;
        redisTemplate.opsForValue().set(key, "true");
        redisTemplate.expire(key, ttl, unit);
    }

    // Check access token in blacklist
    public boolean isAccessTokenBlacklisted(String tokenId) {
        String key = BLACKLIST_ACCESS_TOKEN_PREFIX + tokenId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

}
