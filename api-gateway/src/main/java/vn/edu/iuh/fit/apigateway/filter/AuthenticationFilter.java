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
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<Object> {

    @Value("${JWT_SECRET}")
    private String secret;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static final Map<String, List<String>> PUBLIC_ENDPOINTS = Map.of(
            "GET", List.of(
                    "/api/v1/payments/vn-pay-callback",
                    "/api/v1/rooms",
                    "/v2/api-docs",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-resources",
                    "/swagger-sources/**",
                    "/configuration/ui",
                    "/configuration/security",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/webjars/**",
                    "/api/v1/rooms/amenities",
                    "/api/v1/rooms/target-audiences",
                    "/api/v1/rooms/surrounding-areas",
                    "/api/v1/rooms/**"),
            "POST", List.of(
                    "/api/v1/auth/login",
                    "/api/v1/auth/register",
                    "/api/v1/auth/verify-otp",
                    "/api/v1/auth/refresh",
                    "/api/v1/auth/forgot-password/**",
                    "/api/v1/auth/resend-otp")
    );

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            System.out.println("In api-gateway");

            if (isPublicEndpoint(request.getMethod().name(), request.getURI().getPath())) {
                return chain.filter(exchange);
            }

            // Check header Authorization
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return unauthorizedResponse(exchange.getResponse());
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorizedResponse(exchange.getResponse());
            }

            String token = authHeader.substring(7);
            try {
                Jwts.parser()
                        .verifyWith(getSecretKey())
                        .build()
                        .parseSignedClaims(token);
                System.out.println("Request ok");
                return chain.filter(exchange);
            } catch (Exception e) {
                System.out.println("Request invalid");
                return unauthorizedResponse(exchange.getResponse());
            }
        };
    }

    private boolean isPublicEndpoint(String method, String path) {
        return PUBLIC_ENDPOINTS.getOrDefault(method, List.of())
                .stream()
                .anyMatch(pattern -> path.matches(pattern.replace("**", ".*")));
    }

    private Mono<Void> unauthorizedResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
            {
                "code": "INVALID_TOKEN",
                "message": "Invalid or expired token"
            }
        """;
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

}
