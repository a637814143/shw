package com.example.housekeeping.dto;

import lombok.Data;

/**
 * 管理员DTO
 */
@Data
public class AdminDto {
    
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private Integer status;
    
    public AdminDto() {}
    
    public AdminDto(Long id, String username, String realName, String phone, String email, Integer status) {
        this.id = id;
        this.username = username;
        this.realName = realName;
        this.phone = phone;
        this.email = email;
        this.status = status;
    }
    
    // 手动添加getter方法
    public Long getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getRealName() {
        return realName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public Integer getStatus() {
        return status;
    }
}
