package com.example.housekeeping.dto;

import java.util.Map;

/**
 * 当前登录用户信息响应
 */
public record CurrentUserResponse(
        Long userId,
        String username,
        String userType,
        Map<String, Object> profile
) {
}
