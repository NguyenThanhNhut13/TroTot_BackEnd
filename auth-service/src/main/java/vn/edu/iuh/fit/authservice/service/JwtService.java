/*
 * @ (#) JwtService.java       1.0     09/03/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package vn.edu.iuh.fit.authservice.service;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final String secret;

    public JwtService() {
        Dotenv dotenv = Dotenv.load();
        this.secret = dotenv.get("JWT_SECRET");

        if (this.secret == null || this.secret.isEmpty()) {
            throw new IllegalStateException("JWT_SECRET is not set in .env file");
        }
    }

    public String generateToken(String credential ) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, credential);
    }

    private String createToken(Map<String, Object> claims, String credential) {
        return Jwts.builder()
                .claims(claims)
                .subject(credential)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 30 minutes
                .signWith(getSecretKey())
                .compact();
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
    private boolean validateToken(String token, UserDetails userDetails) {
        final String credential = extractCredential(token);
        return (credential.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
