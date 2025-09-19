package com.brand.artifact.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WebServerException extends RuntimeException {
    private ErrorCode errorCode;
    public WebServerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
