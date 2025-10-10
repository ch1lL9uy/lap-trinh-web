package com.brand.artifact.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.brand.artifact.entity.RefreshToken;
import com.brand.artifact.entity.User;

public interface RefreshTokenService {

    RefreshToken save(User user, String tokenValue, LocalDateTime expiresAt);

    Optional<RefreshToken> findByToken(String tokenValue);

    RefreshToken rotateToken(RefreshToken existingToken, String newTokenValue, LocalDateTime newExpiry);

    void revokeToken(RefreshToken token);

    void revokeToken(String tokenValue);

    void revokeAllTokensForUser(User user);
}
