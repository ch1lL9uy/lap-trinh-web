package com.brand.artifact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brand.artifact.dto.request.UserInfoRequest;
import com.brand.artifact.dto.request.UserLoginRequest;
import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.ResponseAPITemplate;
import com.brand.artifact.dto.response.UserInfoResponse;
import com.brand.artifact.dto.response.UserLoginResponse;
import com.brand.artifact.dto.response.UserRegisterResponse;
import com.brand.artifact.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    public UserService userService;

    @PostMapping("/register")
    public ResponseAPITemplate<UserRegisterResponse> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse userResponse = userService.registerUser(request);
        
        return ResponseAPITemplate.<UserRegisterResponse>builder()
                .code(200) // OK status
                .message("Đăng ký thành công")
                .result(userResponse)
                .build();
    }

    @PostMapping("/login")
    public ResponseAPITemplate<UserLoginResponse> loginUser(@Valid
                                                            @RequestBody UserLoginRequest entity) {

        UserLoginResponse userLoginResponse = userService.authenticateUser(entity.getUsernameOrEmail(), entity.getPassword());

        return ResponseAPITemplate.<UserLoginResponse>builder()
                .code(200) // OK status
                .message("Đăng nhập thành công")
                .result(userLoginResponse)
                .build();   
    }

    @PostMapping("/{userId}/info")
    public ResponseAPITemplate<UserInfoResponse> updateUserInfo(@PathVariable String userId,
            @Valid @RequestBody UserInfoRequest userInfoRequest) {
        // Giả sử bạn đã có phương thức cập nhật thông tin người dùng trong UserService
        UserInfoResponse updatedUserInfo = userService.updateUserInfo(userId, userInfoRequest);

        return ResponseAPITemplate.<UserInfoResponse>builder()
                .code(200) // OK status
                .message("Cập nhật thông tin người dùng thành công")
                .result(updatedUserInfo)
                .build();
    }

    @GetMapping("/{userId}")
    public ResponseAPITemplate<UserInfoResponse> getUserInfo(@PathVariable String userId) {
        UserInfoResponse userInfoResponse = userService.getUserInfo(userId);

        return ResponseAPITemplate.<UserInfoResponse>builder()
                .code(200) // OK status
                .message("Lấy thông tin người dùng thành công")
                .result(userInfoResponse)
                .build();
    }
}