package com.example.housekeeping.controller;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.common.Result;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserManagementController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询用户
     */
    @GetMapping
    public Result<PageResult<User>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<User> result = userService.getUsers(page, size, username, realName, phone, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            // 不返回密码
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            // 不返回密码
            createdUser.setPassword(null);
            return Result.success("用户创建成功", createdUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            // 不返回密码
            updatedUser.setPassword(null);
            return Result.success("用户更新成功", updatedUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success("用户删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用用户
     */
    @PostMapping("/{id}/toggle-status")
    public Result<String> toggleUserStatus(@PathVariable Long id) {
        try {
            userService.toggleUserStatus(id);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置用户密码
     */
    @PostMapping("/{id}/reset-password")
    public Result<String> resetUserPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        try {
            userService.resetUserPassword(id, request.getNewPassword());
            return Result.success("密码重置成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 内部类：重置密码请求
    public static class ResetPasswordRequest {
        private String newPassword;
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
