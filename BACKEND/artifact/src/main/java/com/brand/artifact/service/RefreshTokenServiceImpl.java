package com.brand.artifact.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brand.artifact.entity.RefreshToken;
import com.brand.artifact.entity.User;
import com.brand.artifact.repository.RefreshTokenRepository;
import com.brand.artifact.utils.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public RefreshToken save(User user, String tokenValue, LocalDateTime expiresAt) {
        // Hash the JWT token first to ensure it's under BCrypt's 72-byte limit
        String hashedToken = hashToken(tokenValue);
        String encodedToken = passwordEncoder.encode(hashedToken);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(encodedToken)
                .user(user)
                .expiresAt(expiresAt)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Hash the token using SHA-256 to ensure it's under BCrypt's 72-byte limit
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) {
            return Optional.empty();
        }

        try {
            if (!jwtUtil.isRefreshToken(tokenValue)) {
                return Optional.empty();
            }

            String userId = jwtUtil.getUserIdFromToken(tokenValue);
            if (userId == null || userId.isBlank()) {
                return Optional.empty();
            }

            return refreshTokenRepository.findByUser_UserId(userId).stream()
                    .filter(candidate -> !candidate.isRevoked()
                            && passwordEncoder.matches(hashToken(tokenValue), candidate.getToken()))
                    .findFirst();
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public RefreshToken rotateToken(RefreshToken existingToken, String newTokenValue, LocalDateTime newExpiry) {
        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);
        return save(existingToken.getUser(), newTokenValue, newExpiry);
    }

    @Override
    public void revokeToken(RefreshToken token) {
        if (!token.isRevoked()) {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        }
    }

    @Override
    public void revokeToken(String tokenValue) {
        findByToken(tokenValue).ifPresent(this::revokeToken);
    }

    @Override
    public void revokeAllTokensForUser(User user) {
        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        tokens.forEach(token -> {
            if (!token.isRevoked()) {
                token.setRevoked(true);
            }
        });
        refreshTokenRepository.saveAll(tokens);
    }
}
