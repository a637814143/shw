package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求参数
 */
public record ChangePasswordRequest(
        @NotNull(message = "用户ID不能为空") Long userId,
        @NotBlank(message = "用户类型不能为空") String userType,
        @NotBlank(message = "原密码不能为空") String oldPassword,
        @NotBlank(message = "新密码不能为空")
        @Size(min = 6, max = 100, message = "新密码长度需在6-100个字符之间")
        String newPassword
) {
}
