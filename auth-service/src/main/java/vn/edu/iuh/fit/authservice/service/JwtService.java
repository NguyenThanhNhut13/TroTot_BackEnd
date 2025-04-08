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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.edu.iuh.fit.authservice.model.entity.Role;
import vn.edu.iuh.fit.authservice.model.entity.User;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${JWT_SECRET}")
    private String secret;

    public String generateToken(User user, boolean isRefreshToken ) {
        if (isRefreshToken) {
            return createRefreshToken(user.getId());
        }
        Map<String, Object> claims = new HashMap<>();

        List<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .toList();
        claims.put("roles", String.join(",", roleNames));

        return createToken(claims, user.getId());
    }

    private String createToken(Map<String, Object> claims, Long userId) {
        return Jwts.builder()
                .claims(claims)
                .subject(userId.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 minutes
                .signWith(getSecretKey())
                .compact();
    }

    private String createRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 7))
                .signWith(getSecretKey())
                .compact();
    }

    // Get secret key
    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Get subject
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
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
            return !isTokenExpired(token) ;
        } catch (Exception e) {
            return false;
        }
    }

}
