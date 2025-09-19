package com.brand.artifact.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    
    @NotBlank(message = "Username/Email không được để trống")
    private String usernameOrEmail;
    
    @NotBlank(message = "Password không được để trống")
    private String password;
}