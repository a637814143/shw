package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     */
    @PostMapping("/user/login")
    public Result<Map<String, Object>> userLogin(@RequestBody @Valid LoginRequest request) {
        try {
            Map<String, Object> result = authService.userLogin(request.getUsername(), request.getPassword());
            return Result.success("登录成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 服务者登录
     */
    @PostMapping("/provider/login")
    public Result<Map<String, Object>> providerLogin(@RequestBody @Valid LoginRequest request) {
        try {
            Map<String, Object> result = authService.providerLogin(request.getUsername(), request.getPassword());
            return Result.success("登录成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员登录
     */
    @PostMapping("/admin/login")
    public Result<Map<String, Object>> adminLogin(@RequestBody @Valid LoginRequest request) {
        try {
            Map<String, Object> result = authService.adminLogin(request.getUsername(), request.getPassword());
            return Result.success("登录成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/user/register")
    public Result<Map<String, Object>> userRegister(@RequestBody @Valid RegisterRequest request) {
        try {
            var user = authService.userRegister(request.getUsername(), request.getPassword(), request.getPhone());
            Map<String, Object> result = Map.of("user", user);
            return Result.success("注册成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 服务者注册
     */
    @PostMapping("/provider/register")
    public Result<Map<String, Object>> providerRegister(@RequestBody @Valid RegisterRequest request) {
        try {
            var provider = authService.providerRegister(request.getUsername(), request.getPassword(), request.getPhone());
            Map<String, Object> result = Map.of("provider", provider);
            return Result.success("注册成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 管理员注册
     */
    @PostMapping("/admin/register")
    public Result<Map<String, Object>> adminRegister(@RequestBody @Valid RegisterRequest request) {
        try {
            var admin = authService.adminRegister(request.getUsername(), request.getPassword(), request.getPhone());
            Map<String, Object> result = Map.of("admin", admin);
            return Result.success("注册成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        try {
            authService.changePassword(request.getUserId(), request.getUserType(), 
                request.getOldPassword(), request.getNewPassword());
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 登录请求类
     */
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    /**
     * 注册请求类
     */
    public static class RegisterRequest {
        private String username;
        private String password;
        private String phone;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    /**
     * 修改密码请求类
     */
    public static class ChangePasswordRequest {
        private Long userId;
        private String userType;
        private String oldPassword;
        private String newPassword;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
