/*
 * @ (#) JwtService.java       1.0     09/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.paymentservice.jwt;
/*
 * @description:
 * @author: Nguyen Thanh Nhut
 * @date: 09/03/2025
 * @version:    1.0
 */

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {
    private final String secret;

    public JwtService() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./address-service")
                .ignoreIfMissing()
                .load();
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

    // Get credential
    public String extractCredential(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Get expiration
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        String roles = (String) claims.get("roles");

        if (roles != null && !roles.isEmpty()) {
            return Arrays.asList(roles.split(","));
        }

        return Collections.emptyList();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    // Extract claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Check expiration token
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Valid token
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

}
