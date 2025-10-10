package com.brand.artifact.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brand.artifact.constant.Role;
import com.brand.artifact.dto.request.LogoutRequest;
import com.brand.artifact.dto.request.RefreshTokenRequest;
import com.brand.artifact.dto.request.UserLoginRequest;
import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.TokenRefreshResponse;
import com.brand.artifact.dto.response.UserLoginResponse;
import com.brand.artifact.dto.response.UserRegisterResponse;
import com.brand.artifact.entity.RefreshToken;
import com.brand.artifact.entity.User;
import com.brand.artifact.exception.ErrorCode;
import com.brand.artifact.exception.WebServerException;
import com.brand.artifact.repository.UserRepository;
import com.brand.artifact.utils.JwtUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new WebServerException(ErrorCode.USER_EXISTED);
        }
        if (existsByEmail(request.getEmail())) {
            throw new WebServerException(ErrorCode.EMAIL_EXISTED);
        }

        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new WebServerException(ErrorCode.PASSWORD_MISMATCH);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        user = userRepository.save(user);
        return UserRegisterResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserLoginResponse authenticateUser(UserLoginRequest request) {
        Optional<User> userOpt = findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            userOpt = findByEmail(request.getUsername());
        }
        if (userOpt.isEmpty()) {
            throw new WebServerException(ErrorCode.UNAUTHENTICATED);
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WebServerException(ErrorCode.UNAUTHENTICATED);
        }

        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = jwtUtil.generateRefreshToken(
                user.getUserId(),
                user.getUsername()
        );

        refreshTokenService.revokeAllTokensForUser(user);
        LocalDateTime refreshExpiry = jwtUtil.extractExpiration(refreshToken);
        refreshTokenService.save(user, refreshToken, refreshExpiry);

        return UserLoginResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .tokenType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(jwtUtil.getAccessExpirationMs())
                .refreshToken(refreshToken)
                .refreshTokenExpiresIn(jwtUtil.getRefreshExpirationMs())
                .build();
    }

    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenValue = request.getToken();
        if (!jwtUtil.validateRefreshToken(refreshTokenValue)) {
            throw new WebServerException(ErrorCode.INVALID_TOKEN);
        }

        RefreshToken storedToken = refreshTokenService.findByToken(refreshTokenValue)
                .orElseThrow(() -> new WebServerException(ErrorCode.INVALID_TOKEN));

        if (storedToken.isRevoked() || storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenService.revokeToken(storedToken);
            throw new WebServerException(ErrorCode.TOKEN_EXPIRED);
        }

        User user = storedToken.getUser();

        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );

        String newRefreshToken = jwtUtil.generateRefreshToken(
                user.getUserId(),
                user.getUsername()
        );
        LocalDateTime newRefreshExpiry = jwtUtil.extractExpiration(newRefreshToken);
        refreshTokenService.rotateToken(storedToken, newRefreshToken, newRefreshExpiry);

        return TokenRefreshResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresIn(jwtUtil.getAccessExpirationMs())
                .refreshToken(newRefreshToken)
                .refreshTokenExpiresIn(jwtUtil.getRefreshExpirationMs())
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        if (!jwtUtil.validateRefreshToken(request.getRefreshToken())) {
            throw new WebServerException(ErrorCode.INVALID_TOKEN);
        }
        refreshTokenService.revokeToken(request.getRefreshToken());
        if (request.getAccessToken() != null && !request.getAccessToken().trim().isEmpty()) {
            try {
                long remainingTtl = jwtUtil.getRemainingTtl(request.getAccessToken());
                if (remainingTtl > 0) {
                    tokenBlacklistService.blacklistToken(request.getAccessToken(), remainingTtl);
                }
            } catch (Exception e) {
                log.warn("Unable to blacklist access token on logout", e);
            }
        }
    }

    private boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
