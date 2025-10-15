package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 注册请求参数
 */
@Data
public class AccountRegisterRequest {

    @NotBlank(message = "账号不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "角色不能为空")
    private String role;

    /**
     * 针对用户角色的真实姓名
     */
    private String realName;

    /**
     * 针对用户角色的手机号
     */
    private String phone;

    /**
     * 可选邮箱
     */
    private String email;
}
