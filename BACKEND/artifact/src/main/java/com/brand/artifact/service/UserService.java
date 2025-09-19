package com.brand.artifact.service;

import java.util.List;
import java.util.Optional;

import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.UserResponse;
import com.brand.artifact.entity.User;

public interface UserService {
    
    // ✅ REGISTRATION - Trả về UserResponse (an toàn)
    UserResponse registerUser(UserRegisterRequest request);
    
    // ✅ AUTHENTICATION
    boolean authenticateUser(String usernameOrEmail, String password);
    
    // ✅ FIND METHODS
    Optional<User> findById(String userId);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    // ✅ VALIDATION METHODS (Spring Boot best practice)
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // ✅ CRUD OPERATIONS
    User updateUser(String userId, User updatedUser);
    List<User> getAllUsers();
    boolean deleteUser(String userId);
}
