package com.brand.artifact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.ResponseAPITemplate;
import com.brand.artifact.dto.response.UserResponse;
import com.brand.artifact.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    public UserService userService;

    @PostMapping("/register")
    public ResponseAPITemplate<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse userResponse = userService.registerUser(request);
        
        return ResponseAPITemplate.<UserResponse>builder()
                .code(200) // OK status
                .message("Đăng ký thành công")
                .result(userResponse)
                .build();
    }


}
