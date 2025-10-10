package com.brand.artifact.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseAPITemplate<T> {
    @Builder.Default
    private int code = 200; // Mặc định là 200 OK
    
    @Builder.Default
    private String message = "Success";
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now(); // Thời gian xử lý request
    
    private T result;
}
