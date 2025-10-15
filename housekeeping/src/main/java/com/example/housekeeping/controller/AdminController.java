package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.AdminDto;
import com.example.housekeeping.dto.LoginRequest;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.entity.Admin;
import com.example.housekeeping.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = adminService.login(loginRequest);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取当前管理员信息
     */
    @GetMapping("/profile")
    public Result<AdminDto> getProfile(Authentication authentication) {
        try {
            String username = authentication.getName();
            Admin admin = adminService.getAdminByUsername(username);
            // 转换为DTO，不包含密码
            AdminDto adminDto = new AdminDto(
                admin.getId(),
                admin.getUsername(),
                admin.getRealName(),
                admin.getPhone(),
                admin.getEmail(),
                admin.getStatus()
            );
            return Result.success(adminDto);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestBody ChangePasswordRequest request, 
                                     Authentication authentication) {
        try {
            String username = authentication.getName();
            adminService.changePassword(username, request.getOldPassword(), request.getNewPassword());
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody UpdateProfileRequest request,
                                    Authentication authentication) {
        try {
            String username = authentication.getName();
            adminService.updateProfile(username, request.getRealName(), 
                                     request.getPhone(), request.getEmail());
            return Result.success("个人信息更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 内部类：修改密码请求
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        
        // getters and setters
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
    
    // 内部类：更新个人信息请求
    public static class UpdateProfileRequest {
        private String realName;
        private String phone;
        private String email;
        
        // getters and setters
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
