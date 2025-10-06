package com.example.housekeeping.controller;

import com.example.housekeeping.domain.enums.RoleType;
import com.example.housekeeping.dto.AuthRequest;
import com.example.housekeeping.dto.AuthResponse;
import com.example.housekeeping.dto.RegisterRequest;
import com.example.housekeeping.dto.UpdatePasswordRequest;
import com.example.housekeeping.dto.UpdateProfileRequest;
import com.example.housekeeping.service.AuthService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PutMapping("/{role}/{id}/password")
    public void updatePassword(@PathVariable("role") String role,
            @PathVariable("id") Long id,
            @RequestBody UpdatePasswordRequest request) {
        authService.updatePassword(resolveRole(role), id, request);
    }

    @PutMapping("/{role}/{id}/profile")
    public void updateProfile(@PathVariable("role") String role,
            @PathVariable("id") Long id,
            @RequestBody UpdateProfileRequest request) {
        authService.updateProfile(resolveRole(role), id, request);
    }

    private RoleType resolveRole(String role) {
        try {
            return RoleType.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "未知的角色类型: " + role);
        }
    }
}
