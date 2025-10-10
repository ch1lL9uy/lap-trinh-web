package com.brand.artifact.config.oauth2;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.brand.artifact.entity.User;
import com.brand.artifact.repository.UserRepository;
import com.brand.artifact.service.RefreshTokenService;
import com.brand.artifact.utils.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired private JwtUtil jwtUtil;
    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private UserRepository userRepository;

    // Có thể cấu hình redirect FE qua biến môi trường
    private static final String DEFAULT_REDIRECT_URL = "/"; // hoặc http://localhost:5173/

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        String userId = String.valueOf(principal.getAttributes().get("userId"));
        User user = userRepository.findById(userId).orElseThrow();

        String accessToken = jwtUtil.generateAccessToken(
                user.getUserId(), user.getUsername(), user.getEmail(), user.getRole().name());

        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), user.getUsername());
        LocalDateTime refreshExpiry = jwtUtil.extractExpiration(refreshToken);

        // Revoke toàn bộ refresh cũ và lưu refresh mới
        refreshTokenService.revokeAllTokensForUser(user);
        refreshTokenService.save(user, refreshToken, refreshExpiry);

        // Set cookie HttpOnly cho refresh
        ResponseCookie cookie = ResponseCookie.from("rt", refreshToken)
                .httpOnly(true).secure(true).sameSite("Lax")
                .path("/api/auth") // chỉ gửi khi gọi endpoint auth
                .maxAge(jwtUtil.getRefreshExpirationMs() / 1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Trả JSON cho SPA hoặc redirect nhẹ
        if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            response.setContentType("application/json");
            response.getWriter().write("""
                { "tokenType":"Bearer",
                  "accessToken":"%s",
                  "accessTokenExpiresIn": %d
                }
                """.formatted(accessToken, jwtUtil.getAccessExpirationMs()));
        } else {
            // Trường hợp login qua trình duyệt, redirect về FE kèm access nhỏ gọn (tuỳ ý)
            response.sendRedirect(DEFAULT_REDIRECT_URL + "#access=" + accessToken + "&refresh=" + refreshToken);
        }

        clearAuthenticationAttributes(request);
    }
}
