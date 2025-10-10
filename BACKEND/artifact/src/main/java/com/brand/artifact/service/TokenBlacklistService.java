package com.brand.artifact.service;


public interface TokenBlacklistService {
    void blacklistToken(String token, long ttlMillis);
    boolean isTokenBlacklisted(String token);
    void cleanupExpiredTokens();
}