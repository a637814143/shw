package com.example.housekeeping.dto;

import lombok.Data;

/**
 * 登录响应DTO
 */
@Data
public class LoginResponse {
    
    private String token;
    private String username;
    private String realName;
    private String role;
    
    public LoginResponse(String token, String username, String realName, String role) {
        this.token = token;
        this.username = username;
        this.realName = realName;
        this.role = role;
    }
    
    // 手动添加getter方法
    public String getToken() {
        return token;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public String getRole() {
        return role;
    }
}
