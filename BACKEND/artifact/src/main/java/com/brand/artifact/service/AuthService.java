package com.brand.artifact.service;
import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.UserLoginResponse;
import com.brand.artifact.dto.response.UserRegisterResponse;

public interface AuthService {

    // ✅ REGISTRATION - Trả về UserResponse (an toàn)
    UserRegisterResponse registerUser(UserRegisterRequest request);
    // ✅ AUTHENTICATION
    UserLoginResponse authenticateUser(String usernameOrEmail, String password);

}
