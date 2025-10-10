package com.brand.artifact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brand.artifact.dto.request.LogoutRequest;
import com.brand.artifact.dto.request.RefreshTokenRequest;
import com.brand.artifact.dto.request.UserLoginRequest;
import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.ResponseAPITemplate;
import com.brand.artifact.dto.response.TokenRefreshResponse;
import com.brand.artifact.dto.response.UserLoginResponse;
import com.brand.artifact.dto.response.UserRegisterResponse;
import com.brand.artifact.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseAPITemplate<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse userResponse = authService.registerUser(request);
        return ResponseAPITemplate.<UserRegisterResponse>builder()
                .code(200)
                .message("Đăng ký thành công")
                .result(userResponse)
                .build();
    }

    @PostMapping("/login")
    public ResponseAPITemplate<UserLoginResponse> loginUser(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        UserLoginResponse userLoginResponse = authService.authenticateUser(
                userLoginRequest
        );

        return ResponseAPITemplate.<UserLoginResponse>builder()
                .code(200)
                .message("Đăng nhập thành công")
                .result(userLoginResponse)
                .build();
    }

    @PostMapping("/refresh-token")
    public ResponseAPITemplate<TokenRefreshResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request){
        TokenRefreshResponse response = authService.refreshToken(request);
        return ResponseAPITemplate.<TokenRefreshResponse>builder()
                .code(200)
                .message("Token refreshed successfully")
                .result(response)
                .build();
    }
    
    @PostMapping("/logout")
    public ResponseAPITemplate<String> logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request);
        return ResponseAPITemplate.<String>builder()
                .code(200)
                .message("Logged out successfully")
                .result("User logged out")
                .build();
    }
}
