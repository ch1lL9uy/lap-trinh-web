package com.brand.artifact.service;

import java.util.List;
import java.util.Optional;

import com.brand.artifact.entity.User;

public interface UserService {
    
    // Tạo user mới
    User createUser(User user);
    
    // Tìm user theo ID
    Optional<User> findById(Long userId);
    
    // Tìm user theo username
    Optional<User> findByUsername(String username);
    
    // Tìm user theo email
    Optional<User> findByEmail(String email);
    
    // Authentication
    boolean authenticateUser(String username, String password);
    
    // Update user
    User updateUser(Long userId, User updatedUser);
    
    // Lấy tất cả users
    List<User> getAllUsers();
    
    // Delete user
    boolean deleteUser(Long userId);
}
