package com.brand.artifact.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for authentication and token refresh operations
 * 
 * Fields explanation:
 * - accessTokenExpiresIn: Time remaining in milliseconds (e.g., 3600000 = 1 hour)
 * - refreshTokenExpiresIn: Time remaining in milliseconds (e.g., 604800000 = 7 days)
 * 
 * Frontend can use these values for:
 * - Countdown timers
 * - Automatic token refresh
 * - Session management
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenRefreshResponse implements Serializable {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private Long accessTokenExpiresIn;
    private String refreshToken;
    private Long refreshTokenExpiresIn;
}
    