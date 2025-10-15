package com.example.housekeeping.controller;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.common.Result;
import com.example.housekeeping.entity.Provider;
import com.example.housekeeping.service.ProviderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 家政服务者管理控制器
 */
@RestController
@RequestMapping("/api/admin/providers")
public class ProviderManagementController {
    
    @Autowired
    private ProviderService providerService;
    
    /**
     * 分页查询服务者
     */
    @GetMapping
    public Result<PageResult<Provider>> getProviders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<Provider> result = providerService.getProviders(page, size, username, realName, phone, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID获取服务者
     */
    @GetMapping("/{id}")
    public Result<Provider> getProviderById(@PathVariable Long id) {
        try {
            Provider provider = providerService.getProviderById(id);
            // 不返回密码
            provider.setPassword(null);
            return Result.success(provider);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建服务者
     */
    @PostMapping
    public Result<Provider> createProvider(@Valid @RequestBody Provider provider) {
        try {
            Provider createdProvider = providerService.createProvider(provider);
            // 不返回密码
            createdProvider.setPassword(null);
            return Result.success("服务者创建成功", createdProvider);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新服务者
     */
    @PutMapping("/{id}")
    public Result<Provider> updateProvider(@PathVariable Long id, @Valid @RequestBody Provider provider) {
        try {
            Provider updatedProvider = providerService.updateProvider(id, provider);
            // 不返回密码
            updatedProvider.setPassword(null);
            return Result.success("服务者更新成功", updatedProvider);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除服务者
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteProvider(@PathVariable Long id) {
        try {
            providerService.deleteProvider(id);
            return Result.success("服务者删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核服务者
     */
    @PostMapping("/{id}/audit")
    public Result<String> auditProvider(@PathVariable Long id, @RequestBody AuditRequest request) {
        try {
            providerService.auditProvider(id, request.getStatus(), request.getRemark());
            return Result.success("审核完成");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置服务者密码
     */
    @PostMapping("/{id}/reset-password")
    public Result<String> resetProviderPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        try {
            providerService.resetProviderPassword(id, request.getNewPassword());
            return Result.success("密码重置成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    // 内部类：审核请求
    public static class AuditRequest {
        private Integer status;
        private String remark;
        
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
    }
    
    // 内部类：重置密码请求
    public static class ResetPasswordRequest {
        private String newPassword;
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
