package com.example.housekeeping.controller;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.dto.request.LoginRequest;
import com.example.housekeeping.dto.request.RegisterRequest;
import com.example.housekeeping.dto.response.LoginResponse;
import com.example.housekeeping.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/login")
    public ApiResponse<LoginResponse> adminLogin(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authService.loginAdmin(request));
    }

    @PostMapping("/provider/login")
    public ApiResponse<LoginResponse> providerLogin(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authService.loginProvider(request));
    }

    @PostMapping("/user/login")
    public ApiResponse<LoginResponse> userLogin(@RequestBody @Valid LoginRequest request) {
        return ApiResponse.success(authService.loginUser(request));
    }

    @PostMapping("/user/register")
    public ApiResponse<LoginResponse> userRegister(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(authService.registerUser(request));
    }

    @PostMapping("/provider/register")
    public ApiResponse<LoginResponse> providerRegister(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(authService.registerProvider(request));
    }
}
