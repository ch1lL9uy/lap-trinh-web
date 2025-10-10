package com.brand.artifact.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void blacklistToken(String token, long ttlMillis) {
        redisTemplate.opsForValue().set(token, "blacklisted", ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    @Override
    public void cleanupExpiredTokens() {
    }
}
