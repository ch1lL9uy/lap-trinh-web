package com.brand.artifact.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brand.artifact.dto.response.ResponseAPITemplate;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ResponseAPITemplate<String> publicEndpoint() {
        return ResponseAPITemplate.<String>builder()
                .code(200)
                .message("Public endpoint accessed successfully")
                .result("This endpoint is accessible to everyone")
                .build();
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseAPITemplate<String> userEndpoint(Authentication authentication) {
        return ResponseAPITemplate.<String>builder()
                .code(200)
                .message("User endpoint accessed successfully")
                .result("Hello " + authentication.getName() + "! You are authenticated with roles: " + authentication.getAuthorities())
                .build();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseAPITemplate<String> adminEndpoint(Authentication authentication) {
        return ResponseAPITemplate.<String>builder()
                .code(200)
                .message("Admin endpoint accessed successfully")
                .result("Hello Admin " + authentication.getName() + "! This is admin-only content")
                .build();
    }

    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseAPITemplate<String> staffEndpoint(Authentication authentication) {
        return ResponseAPITemplate.<String>builder()
                .code(200)
                .message("Staff endpoint accessed successfully")
                .result("Hello Staff/Admin " + authentication.getName() + "! This is staff-level content")
                .build();
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseAPITemplate<String> getProfile(Authentication authentication) {
        String profileInfo = String.format("Username: %s, Authorities: %s", 
                                         authentication.getName(), 
                                         authentication.getAuthorities().toString());
        return ResponseAPITemplate.<String>builder()
                .code(200)
                .message("Profile retrieved successfully")
                .result(profileInfo)
                .build();
    }
}