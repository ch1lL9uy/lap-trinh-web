package com.brand.artifact.utils;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.access.secret:a-string-secret-at-least-256-bits-long-for-jwt-token-generation}")
    private String accessSecret;

    @Value("${jwt.refresh.secret:}")
    private String refreshSecret;

    @Value("${jwt.access.expiration-ms:900000}")
    private long accessExpirationMs;

    @Value("${jwt.refresh.expiration-ms:604800000}")
    private long refreshExpirationMs;

    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private Key getAccessSigningKey() {
        return getSigningKey(accessSecret);
    }

    private Key getRefreshSigningKey() {
        String effectiveSecret = (refreshSecret == null || refreshSecret.isBlank()) ? accessSecret : refreshSecret;
        return getSigningKey(effectiveSecret);
    }

    private Key getSigningKey(String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters.");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateAccessToken(String userId, String username, String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .issuer("ptithcm.edu")
                .claim("userId", userId)
                .claim("email", email)
                .claim("role", role)
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getAccessSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim(CLAIM_TYPE, TYPE_REFRESH)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getRefreshSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return parseAccessClaims(token) != null;
    }

    public boolean validateAccessToken(String token, String username) {
        Claims claims = parseAccessClaims(token);
        return claims != null && username.equals(claims.getSubject());
    }

    public boolean validateRefreshToken(String token) {
        return parseRefreshClaims(token) != null;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = resolveClaims(token);
        return claims != null ? claims.getSubject() : null;
    }

    public String getUserIdFromToken(String token) {
        Claims claims = resolveClaims(token);
        return claims != null ? claims.get("userId", String.class) : null;
    }

    public String getEmailFromToken(String token) {
        Claims claims = parseAccessClaims(token);
        return claims != null ? claims.get("email", String.class) : null;
    }

    public String getRoleFromToken(String token) {
        Claims claims = parseAccessClaims(token);
        return claims != null ? claims.get("role", String.class) : null;
    }

    public Date getExpirationFromToken(String token) {
        Claims claims = resolveClaims(token);
        return claims != null ? claims.getExpiration() : null;
    }

    public LocalDateTime extractExpiration(String token) {
        Claims claims = resolveClaims(token);
        if (claims == null) {
            return LocalDateTime.now().plusDays(1);
        }
        return claims.getExpiration().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseRefreshClaims(token);
        return claims != null && TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public boolean isAccessToken(String token) {
        Claims claims = parseAccessClaims(token);
        return claims != null && TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    private Claims parseAccessClaims(String token) {
        return parseClaims(token, getAccessSigningKey(), TYPE_ACCESS);
    }

    private Claims parseRefreshClaims(String token) {
        return parseClaims(token, getRefreshSigningKey(), TYPE_REFRESH);
    }

    private Claims resolveClaims(String token) {
        Claims claims = parseAccessClaims(token);
        if (claims != null) {
            return claims;
        }
        return parseRefreshClaims(token);
    }

    private Claims parseClaims(String token, Key key, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            if (claims.getExpiration().before(new Date())) {
                return null;
            }
            String type = claims.get(CLAIM_TYPE, String.class);
            if (expectedType != null && !expectedType.equals(type)) {
                return null;
            }
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public long getRemainingTtl(String token) {
        try {
            Claims claims = resolveClaims(token);
            if (claims == null) {
                return 0; 
            }
            
            Date expiration = claims.getExpiration();
            Date now = new Date();
            
            if (expiration.before(now)) {
                return 0;
            }
            
            return expiration.getTime() - now.getTime();
        } catch (Exception e) {
            return 0; 
        }
    }
}
