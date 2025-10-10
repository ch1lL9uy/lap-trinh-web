package com.brand.artifact.service;

import java.util.List;
import java.util.Optional;

import com.brand.artifact.dto.request.UserInfoRequest;
import com.brand.artifact.dto.response.UserInfoResponse;
import com.brand.artifact.entity.User;

public interface UserService {
    Optional<User> findById(String userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    User updateUser(String userId, User updatedUser);
    List<User> getAllUsers();
    boolean deleteUser(String userId);

    UserInfoResponse getUserInfo(String userId);
    UserInfoResponse updateUserInfo(String userId, UserInfoRequest userInfoRequest);
}
