package com.brand.artifact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brand.artifact.dto.request.UserInfoRequest;
import com.brand.artifact.dto.response.ResponseAPITemplate;
import com.brand.artifact.dto.response.UserInfoResponse;
import com.brand.artifact.service.UserService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    public UserService userService;

    @PostMapping("/{userId}/info")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or (hasRole('USER'))")
    public ResponseAPITemplate<UserInfoResponse> updateUserInfo(@PathVariable String userId,
            @Valid @RequestBody UserInfoRequest userInfoRequest, Authentication authentication) {
        // Giả sử bạn đã có phương thức cập nhật thông tin người dùng trong UserService
        UserInfoResponse updatedUserInfo = userService.updateUserInfo(userId, userInfoRequest);

        return ResponseAPITemplate.<UserInfoResponse>builder()
                .code(200) // OK status
                .message("Cập nhật thông tin người dùng thành công")
                .result(updatedUserInfo)
                .build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseAPITemplate<UserInfoResponse> getUserInfo(@PathVariable String userId, Authentication authentication) {
        UserInfoResponse userInfoResponse = userService.getUserInfo(userId);

        return ResponseAPITemplate.<UserInfoResponse>builder()
                .code(200) // OK status
                .message("Lấy thông tin người dùng thành công")
                .result(userInfoResponse)
                .build();
    }
    
}