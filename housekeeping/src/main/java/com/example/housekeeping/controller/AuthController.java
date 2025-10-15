package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.LoginRequest;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器（测试用）
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 测试登录（硬编码用户）
     */
    @PostMapping("/test-login")
    public Result<LoginResponse> testLogin(@RequestBody LoginRequest loginRequest) {
        try {
            // 硬编码测试用户
            if ("admin".equals(loginRequest.getUsername()) && "admin123".equals(loginRequest.getPassword())) {
                String token = jwtUtil.generateToken("admin", "ADMIN");
                LoginResponse response = new LoginResponse(token, "admin", "系统管理员", "ADMIN");
                return Result.success("登录成功", response);
            } else {
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            return Result.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试JWT验证
     */
    @GetMapping("/test-verify")
    public Result<Map<String, Object>> testVerify(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            
            Map<String, Object> data = new HashMap<>();
            data.put("username", username);
            data.put("role", role);
            data.put("valid", true);
            
            return Result.success("Token验证成功", data);
        } catch (Exception e) {
            return Result.error("Token验证失败: " + e.getMessage());
        }
    }
}
