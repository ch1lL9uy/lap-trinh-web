package com.brand.artifact.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.brand.artifact.service.CustomUserDetailsService;
import com.brand.artifact.service.TokenBlacklistService;
import com.brand.artifact.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    // Danh sách các path cần BỎ QUA JWT filter (OAuth2 paths)
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "/oauth2/",
            "/login/oauth2/",
            "/auth/",
            "/api/auth/"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Bỏ qua OAuth2 và auth endpoints
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        
        // Nếu không có Authorization header hoặc không phải Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        
        // Kiểm tra token có bị blacklist không
        if (tokenBlacklistService.isTokenBlacklisted(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Validate access token
        if (!jwtUtil.isAccessToken(jwt) || !jwtUtil.validateAccessToken(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtil.getUsernameFromToken(jwt);
            
            // Nếu chưa có authentication trong context, tạo mới
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateAccessToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (UsernameNotFoundException ignored) {
            // Username không tồn tại, bỏ qua
        }

        filterChain.doFilter(request, response);
    }
}