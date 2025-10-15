package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 家政服务者实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "provider")
public class Provider extends BaseEntity {
    
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
    
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    @Column(name = "id_card", length = 18)
    private String idCard;
    
    @Column(name = "id_card_front")
    private String idCardFront;
    
    @Column(name = "id_card_back")
    private String idCardBack;
    
    @Column(name = "certificate")
    private String certificate;
    
    @Column(name = "experience", columnDefinition = "TEXT")
    private String experience;
    
    @Column(name = "balance", precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Column(name = "service_count")
    private Integer serviceCount = 0;
    
    @Column(name = "status")
    private Integer status = 0; // 0-待审核，1-已通过，2-已拒绝，3-已禁用
    
    // 手动添加setPassword方法（如果Lombok没有正确生成）
    public void setPassword(String password) {
        this.password = password;
    }
}
