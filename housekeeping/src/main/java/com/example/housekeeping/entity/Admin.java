package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admin")
public class Admin extends BaseEntity {
    
    @NotBlank(message = "用户名不能为空")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Column(name = "password", nullable = false)
    private String password;
    
    @NotBlank(message = "真实姓名不能为空")
    @Column(name = "real_name", nullable = false, length = 50)
    private String realName;
    
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用，1-启用
    
    // 手动添加getter和setter方法以确保Lombok正常工作
    public Long getId() {
        return super.getId();
    }
    
    public void setId(Long id) {
        super.setId(id);
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public void setRealName(String realName) {
        this.realName = realName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
}
