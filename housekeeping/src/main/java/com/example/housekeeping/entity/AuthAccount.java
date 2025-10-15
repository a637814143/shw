package com.example.housekeeping.entity;

import com.example.housekeeping.enums.AccountRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录认证账号实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "auth_account")
public class AuthAccount extends BaseEntity {

    @NotBlank(message = "账号不能为空")
    @Column(name = "account", unique = true, nullable = false, length = 50)
    private String account;

    @NotBlank(message = "密码不能为空")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false, length = 20)
    private AccountRole role;
}
