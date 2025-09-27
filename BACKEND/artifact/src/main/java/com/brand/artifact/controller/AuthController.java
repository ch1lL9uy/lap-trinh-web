package com.brand.artifact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brand.artifact.dto.request.UserLoginRequest;
import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.ResponseAPITemplate;
import com.brand.artifact.dto.response.UserLoginResponse;
import com.brand.artifact.dto.response.UserRegisterResponse;
import com.brand.artifact.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    public AuthService authService;

    @PostMapping("/register")
    public ResponseAPITemplate<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse userResponse = authService.registerUser(request);

        return ResponseAPITemplate.<UserRegisterResponse>builder()
                .code(200) // OK status
                .message("Đăng ký thành công")
                .result(userResponse)
                .build();
    }

    @PostMapping("/login")
    public ResponseAPITemplate<UserLoginResponse> loginUser(@Valid
                                                            @RequestBody UserLoginRequest entity) {

        UserLoginResponse userLoginResponse = authService.authenticateUser(entity.getUsername(), entity.getPassword());

        return ResponseAPITemplate.<UserLoginResponse>builder()
                .code(200) // OK status
                .message("Đăng nhập thành công")
                .result(userLoginResponse)
                .build();   
    }


}
