package com.brand.artifact.exception;

import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.brand.artifact.dto.response.ResponseAPITemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalHandler {
    private ResponseEntity<ResponseAPITemplate<String>> handleException (Exception e, HttpStatus status) {
		return ResponseEntity.status(status)
				.body(ResponseAPITemplate.<String>builder()
                        .code(status.value())
                        .message(e.getMessage())
                        .result(null)
                        .build());
	}

    @ExceptionHandler(WebServerException.class)
	public ResponseEntity<ResponseAPITemplate<String>> handleWebServerException(WebServerException e) {
		return handleException(e, HttpStatus.resolve(e.getErrorCode().getHttpStatusCode().value()));
	}
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseAPITemplate<String>> handleWebServerException(IllegalArgumentException e) {
		return handleException(e, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ResponseAPITemplate<String>> handleAccessDeniedException(AccessDeniedException e) {
		return handleException(e, HttpStatus.FORBIDDEN);
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseAPITemplate<String>> handleException(Exception e) {
		log.error("Unexpected exception", e);
		return handleException(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
