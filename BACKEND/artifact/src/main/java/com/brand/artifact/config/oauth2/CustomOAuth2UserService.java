package com.brand.artifact.config.oauth2;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.brand.artifact.constant.AuthProvider;
import com.brand.artifact.constant.Role;
import com.brand.artifact.entity.User;
import com.brand.artifact.entity.UserInfo;
import com.brand.artifact.exception.ErrorCode;
import com.brand.artifact.exception.WebServerException;
import com.brand.artifact.repository.UserInfoRepository;
import com.brand.artifact.repository.UserRepository;


@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attrs = oAuth2User.getAttributes();
        
        String providerId;
        String email = null;
        String avatar = null;
        String firstName = null;
        String lastName = null;
        boolean emailVerified = false;
        AuthProvider provider;

        
        switch (registrationId) {
            case "google" -> {
                provider = AuthProvider.GOOGLE;
                providerId = (String) attrs.get("sub");
                email = (String) attrs.get("email");
                Object ev = attrs.get("email_verified");
                emailVerified = ev instanceof Boolean ? (Boolean) ev : "true".equalsIgnoreCase(String.valueOf(ev));
                avatar = (String) attrs.get("picture");
                firstName = (String) attrs.get("given_name");
                lastName = (String) attrs.get("family_name");
            }
            case "facebook" -> {
                provider = AuthProvider.FACEBOOK;
                providerId = String.valueOf(attrs.get("id"));
                // Facebook có thể không trả email nếu app bạn chưa được approve scope email
                email = (String) attrs.get("email");
                avatar = nested(attrs, "picture", "data", "url");
                String name = (String) attrs.get("name");
                if (name != null && name.contains(" ")) {
                    int i = name.indexOf(' ');
                    firstName = name.substring(0, i);
                    lastName = name.substring(i + 1);
                } else {
                    firstName = name;
                }
                emailVerified = true;
            }
            default -> throw new WebServerException(ErrorCode.GOOGLE_LOGIN_FAILED);
        }
        
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            // Tạo user mới
            user = User.builder()
                    .username(genUsernameFromEmailOrName(email, firstName))
                    .email(email)
                    .authProvider(provider)
                    .providerId(providerId)
                    .emailVerified(emailVerified)
                    .role(Role.USER)
                    .lastLoginAt(LocalDateTime.now())
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();
            
            user = userRepository.save(user);
            
            // Tạo UserInfo nếu có thông tin
            if (firstName != null || lastName != null || avatar != null) {
                UserInfo userInfo = UserInfo.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarUrl(avatar)
                        .user(user)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                userInfoRepository.save(userInfo);
                user.setUserInfo(userInfo);
            }
        } else {
            if (user.getAuthProvider() == AuthProvider.LOCAL && user.getProviderId() == null) {
                user.setAuthProvider(provider);
                user.setProviderId(providerId);
            }
            user.setEmailVerified(Boolean.TRUE.equals(user.getEmailVerified()) || emailVerified);
            user.setLastLoginAt(LocalDateTime.now());
            if (user.getUserInfo() != null) {
                if (firstName != null) user.getUserInfo().setFirstName(firstName);
                if (lastName != null)  user.getUserInfo().setLastName(lastName);
                if (avatar != null)    user.getUserInfo().setAvatarUrl(avatar);
                user.getUserInfo().setUpdatedAt(LocalDateTime.now());
            } else if (firstName != null || lastName != null || avatar != null) {
                UserInfo userInfo = UserInfo.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .avatarUrl(avatar)
                        .user(user)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                userInfoRepository.save(userInfo);
                user.setUserInfo(userInfo);
            }
            userRepository.save(user);
        }
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        Map<String, Object> principalAttrs = new HashMap<>(attrs);
        principalAttrs.put("userId", user.getUserId());
        String nameAttrKey = registrationId.equals("google") ? "sub" : "id";

        return new DefaultOAuth2User(authorities, principalAttrs, nameAttrKey);
    } 

    private static String genUsernameFromEmailOrName(String email, String firstName){
        if (email != null && email.contains(("@"))){
            String base = email.substring(0, email.indexOf("@")).replaceAll("[^a-zA-Z0-9._-]", "");
            return (base.length() >= 3 ? base : ("user" + System.currentTimeMillis()));
        }
        String base = (firstName != null ? firstName : "user");
        return (base + System.currentTimeMillis()).replaceAll("\\s+", "");
    }


    @SuppressWarnings("unchecked")
    private static String nested(Map<String, Object> map, String... keys) {
        Object cur = map;
        for (String k : keys) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(k);
            if (cur == null) return null;
        }
        return String.valueOf(cur);
    }
}