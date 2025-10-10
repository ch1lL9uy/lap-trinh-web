package com.brand.artifact.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest implements Serializable{

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
    // Optional - for blacklisting access token during logout
    private String accessToken;
}
