package com.brand.artifact.dto.response;

import com.brand.artifact.constant.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterResponse {
    private String userId;
    private String email;
    private Role role;

}