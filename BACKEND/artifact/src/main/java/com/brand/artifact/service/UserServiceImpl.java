package com.brand.artifact.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brand.artifact.constant.Role;
import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.UserResponse;
import com.brand.artifact.entity.User;
import com.brand.artifact.exception.ErrorCode;
import com.brand.artifact.exception.WebServerException;
import com.brand.artifact.repository.UserRepository;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRegisterRequest request) {
        // ✅ Validation sử dụng custom exception
        if (existsByUsername(request.getUsername())) {
            throw new WebServerException(ErrorCode.USER_EXISTED);
        }
        if (existsByEmail(request.getEmail())) {
            throw new WebServerException(ErrorCode.USER_EXISTED);
        }

        // ✅ Convert DTO to Entity (mapping pattern)
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // ✅ Encrypt password
                .role(Role.USER) // ✅ Default role
                .build();

        user = userRepository.save(user);
        
        // ✅ Convert Entity to Response DTO (KHÔNG trả password)
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public boolean authenticateUser(String usernameOrEmail, String rawPassword) {
        // ✅ Support login bằng username hoặc email
        Optional<User> userOpt = findByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = findByEmail(usernameOrEmail);
        }
        
        if (userOpt.isPresent()) {
            // ✅ Sử dụng PasswordEncoder để verify
            return passwordEncoder.matches(rawPassword, userOpt.get().getPassword());
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User updateUser(String userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new WebServerException(ErrorCode.USER_NOT_FOUND));
        
        // ✅ Chỉ update các field được phép
        existingUser.setEmail(updatedUser.getEmail());
        // Note: Password update nên có endpoint riêng với validation
        
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new WebServerException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
        return true;
    }
}
