package com.brand.artifact.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brand.artifact.constant.Role;
import com.brand.artifact.dto.request.UserRegisterRequest;
import com.brand.artifact.dto.response.UserLoginResponse;
import com.brand.artifact.dto.response.UserRegisterResponse;
import com.brand.artifact.entity.User;
import com.brand.artifact.exception.ErrorCode;
import com.brand.artifact.exception.WebServerException;
import com.brand.artifact.repository.UserRepository;
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private com.brand.artifact.utils.JwtUtil jwtUtil;


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
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
    // LOGIN
    @Override
    public UserLoginResponse authenticateUser(String username, String rawPassword) {
        if (existsByUsername(username)) {
            Optional<User> userOpt = findByUsername(username);
            if (userOpt.isPresent() && passwordEncoder.matches(rawPassword, userOpt.get().getPassword())) {
                User user = userOpt.get();
                // JWT Token
                String token = jwtUtil.generateToken(
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name()
                );
                
                return UserLoginResponse.builder()
                        .userId(user.getUserId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .token(token)
                        .build();
            }
        }
        return null;
    }

    private boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    private boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    private Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
