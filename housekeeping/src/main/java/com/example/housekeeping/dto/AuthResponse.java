package com.example.housekeeping.dto;

import java.util.Map;

/**
 * 登录成功响应
 */
public record AuthResponse(
        String token,
        long expiresAt,
        String userType,
        Map<String, Object> profile
) {
}
