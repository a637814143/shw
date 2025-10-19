package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户或家政公司修改密码时使用的请求体。
 */
public class AccountPasswordUpdateRequest {

    @NotBlank(message = "原密码不能为空")
    @Size(min = 4, max = 64, message = "原密码长度需在4-64个字符之间")
    private String currentPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 4, max = 64, message = "新密码长度需在4-64个字符之间")
    private String newPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
