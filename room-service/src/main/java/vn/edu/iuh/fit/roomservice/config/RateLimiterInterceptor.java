/*
 * @ (#) RateLimiterInterceptor.java       1.0     13/05/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.roomservice.config;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 13/05/2025
 * @version:    1.0
 */

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RateLimiterInterceptor implements RequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterInterceptor.class);
    private final RateLimiter rateLimiter;

    public RateLimiterInterceptor(RateLimiterRegistry rateLimiterRegistry) {
        this.rateLimiter = rateLimiterRegistry.rateLimiter("addressServiceRateLimiter");
        logger.info("RateLimiter config: limitForPeriod={}, limitRefreshPeriod={}",
                rateLimiter.getRateLimiterConfig().getLimitForPeriod(),
                rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod());
    }

    @Override
    public void apply(RequestTemplate template) {
        try {
            rateLimiter.acquirePermission();
            logger.debug("RateLimiter permission acquired for: {}", template.url());
        } catch (RequestNotPermitted ex) {
            logger.error("Rate limit exceeded for request: {}", template.url(), ex);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
    }
}
