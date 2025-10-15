package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 普通用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user")
public class User extends BaseEntity {
    
    @NotBlank(message = "用户名不能为空")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Column(name = "password", nullable = false)
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Column(name = "phone", unique = true, nullable = false, length = 20)
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用，1-启用
}
