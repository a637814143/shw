package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.AuthResponse;
import com.example.housekeeping.dto.ChangePasswordRequest;
import com.example.housekeeping.dto.CurrentUserResponse;
import com.example.housekeeping.dto.LoginRequest;
import com.example.housekeeping.dto.RegisterRequest;
import com.example.housekeeping.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/user/login")
    public Result<AuthResponse> userLogin(@RequestBody @Valid LoginRequest request) {
        try {
            AuthResponse response = authService.userLogin(request.username(), request.password());
            return Result.success("登录成功", response);
        } catch (IllegalArgumentException ex) {
            return Result.error(400, ex.getMessage());
        }
    }

    /**
     * 服务者登录
     */
    @PostMapping("/provider/login")
    public Result<AuthResponse> providerLogin(@RequestBody @Valid LoginRequest request) {
        try {
            AuthResponse response = authService.providerLogin(request.username(), request.password());
            return Result.success("登录成功", response);
        } catch (IllegalArgumentException ex) {
            return Result.error(400, ex.getMessage());
        }
    }

    /**
     * 管理员登录
     */
    @PostMapping("/admin/login")
    public Result<AuthResponse> adminLogin(@RequestBody @Valid LoginRequest request) {
        try {
            AuthResponse response = authService.adminLogin(request.username(), request.password());
            return Result.success("登录成功", response);
        } catch (IllegalArgumentException ex) {
            return Result.error(400, ex.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/user/register")
    public Result<?> userRegister(@RequestBody @Valid RegisterRequest request) {
        try {
            return Result.success("注册成功", authService.userRegister(request.username(), request.password(), request.phone()));
        } catch (IllegalArgumentException ex) {
            return Result.error(400, ex.getMessage());
        }
    }

    /**
     * 服务者注册
     */
    @PostMapping("/provider/register")
    public Result<?> providerRegister(@RequestBody @Valid RegisterRequest request) {
        try {
            return Result.success("注册成功", authService.providerRegister(request.username(), request.password(), request.phone()));
        } catch (IllegalArgumentException ex) {
            return Result.error(400, ex.getMessage());
        }
    }

    /**
     * 管理员注册
     */
    @PostMapping("/admin/register")
    public Result<?> adminRegister(@RequestBody @Valid RegisterRequest request) {
        try {
            return Result.success("注册成功", authService.adminRegister(request.username(), request.password(), request.phone()));
        } catch (IllegalArgumentException ex) {
            return Result.error(400, ex.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                         HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        String userType = (String) httpRequest.getAttribute("userType");

        if (userId == null || userType == null) {
            return Result.unauthorized("未授权访问，请先登录");
        }

        if (!Objects.equals(userId, request.userId()) || !userType.equalsIgnoreCase(request.userType())) {
            return Result.forbidden("用户身份不匹配，无法修改密码");
        }

        try {
            authService.changePassword(userId, userType, request.oldPassword(), request.newPassword());
            return Result.success("密码修改成功");
        } catch (IllegalArgumentException ex) {
            return Result.error(400, ex.getMessage());
        }
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public Result<CurrentUserResponse> currentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String userType = (String) request.getAttribute("userType");

        if (userId == null || userType == null) {
            return Result.unauthorized("未授权访问，请先登录");
        }

        try {
            CurrentUserResponse response = authService.loadCurrentUser(userId, userType);
            return Result.success(response);
        } catch (IllegalArgumentException ex) {
            return Result.error(ex.getMessage());
        }
    }
}
