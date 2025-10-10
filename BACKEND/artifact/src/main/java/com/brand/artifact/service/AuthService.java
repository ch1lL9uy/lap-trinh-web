package com.brand.artifact.service;

import com.brand.artifact.dto.request.LogoutRequest;
import com.brand.artifact.dto.request.RefreshTokenRequest;
import com.brand.artifact.dto.request.UserLoginRequest;
import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.TokenRefreshResponse;
import com.brand.artifact.dto.response.UserLoginResponse;
import com.brand.artifact.dto.response.UserRegisterResponse;

public interface AuthService {

    UserRegisterResponse registerUser(UserRegisterRequest request);

    UserLoginResponse authenticateUser(UserLoginRequest request);

    TokenRefreshResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}
