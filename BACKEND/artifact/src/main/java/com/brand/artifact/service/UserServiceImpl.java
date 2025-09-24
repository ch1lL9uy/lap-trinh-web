package com.brand.artifact.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brand.artifact.constant.Role;
import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.UserInfoResponse;
import com.brand.artifact.dto.response.UserLoginResponse;
import com.brand.artifact.dto.response.UserRegisterResponse;
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
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new WebServerException(ErrorCode.USER_EXISTED);
        }
        if (existsByEmail(request.getEmail())) {
            throw new WebServerException(ErrorCode.EMAIL_EXISTED);
        }

        if (request.getPassword() == null || !request.getPassword().equals(request.getConfirmPassword())) {
            throw new WebServerException(ErrorCode.PASSWORD_MISMATCH);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER) 
                .build();
        
        user = userRepository.save(user);
        return UserRegisterResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserLoginResponse authenticateUser(String usernameOrEmail, String rawPassword) {
        if (existsByEmail(usernameOrEmail) || existsByUsername(usernameOrEmail)) {
            Optional<User> userOpt = findByEmail(usernameOrEmail);
            if (userOpt.isEmpty()) {
                userOpt = findByUsername(usernameOrEmail);
            }
            if (userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword())) {
                User user = userOpt.get();
                return UserLoginResponse.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .build();
            }
        }
        return null;
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
        existingUser.setEmail(updatedUser.getEmail());
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

    // Lấy thông tin UserInfo
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new WebServerException(ErrorCode.USER_NOT_FOUND));
        if (user.getUserInfo() == null) {
            throw new WebServerException(ErrorCode.USER_INFO_NOT_FOUND);
        }
        return UserInfoResponse.builder()
                .firstName(user.getUserInfo().getFirstName())
                .lastName(user.getUserInfo().getLastName())
                .phone(user.getUserInfo().getPhone())
                .email(user.getEmail())
                .build();
    }
}
