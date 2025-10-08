package com.example.housekeeping.controller;

import com.example.housekeeping.dto.LoginRequest;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.dto.RegisterRequest;
import com.example.housekeeping.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "注册成功");
        return ResponseEntity.status(201).body(body);
    }
}
