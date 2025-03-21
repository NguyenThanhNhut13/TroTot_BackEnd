/*
 * @ (#) JwtAuthFilter.java       1.0     18/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.apigateway.filter;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 18/03/2025
 * @version:    1.0
 */

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Component
public class JwtAuthFilter implements WebFilter {

    private final String secret;

    public JwtAuthFilter() {
        Dotenv dotenv = Dotenv.load();
        this.secret = dotenv.get("JWT_SECRET");

        if (this.secret == null || this.secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET is not set in .env file");
        }
    }

    // Get secret key
    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    @Override
    public @Nonnull Mono<Void> filter(@Nonnull ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Check Authorization header
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            return unauthorizedResponse(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange);
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            String roles = claims.get("roles", String.class);

            // Add user information into request header so other services use
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-User-Roles", roles)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            return unauthorizedResponse(exchange);
        }

    }

    private Mono<Void> unauthorizedResponse(@Nonnull ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
