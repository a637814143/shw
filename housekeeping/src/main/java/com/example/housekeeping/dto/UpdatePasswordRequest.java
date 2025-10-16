package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 管理员重置密码请求。
 */
public class UpdatePasswordRequest {

    @NotBlank(message = "密码不能为空")
    @Size(min = 4, max = 64, message = "密码长度应在4-64个字符之间")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
