package com.example.housekeeping.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            body.put("message", fieldError.getDefaultMessage());
        } else {
            body.put("message", "请求参数不合法");
        }
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentials(InvalidCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", "服务器内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
