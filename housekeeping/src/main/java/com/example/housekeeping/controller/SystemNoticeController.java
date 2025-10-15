package com.example.housekeeping.controller;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.common.Result;
import com.example.housekeeping.entity.SystemNotice;
import com.example.housekeeping.service.SystemNoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统公告控制器
 */
@RestController
@RequestMapping("/api/admin/notices")
public class SystemNoticeController {
    
    @Autowired
    private SystemNoticeService systemNoticeService;
    
    /**
     * 分页查询公告
     */
    @GetMapping
    public Result<PageResult<SystemNotice>> getNotices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<SystemNotice> result = systemNoticeService.getNotices(page, size, title, type, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取启用的公告列表（首页用）
     */
    @GetMapping("/active")
    public Result<List<SystemNotice>> getActiveNotices() {
        try {
            List<SystemNotice> notices = systemNoticeService.getActiveNotices();
            return Result.success(notices);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID获取公告
     */
    @GetMapping("/{id}")
    public Result<SystemNotice> getNoticeById(@PathVariable Long id) {
        try {
            SystemNotice notice = systemNoticeService.getNoticeById(id);
            return Result.success(notice);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建公告
     */
    @PostMapping
    public Result<SystemNotice> createNotice(@Valid @RequestBody SystemNotice notice) {
        try {
            SystemNotice createdNotice = systemNoticeService.createNotice(notice);
            return Result.success("公告创建成功", createdNotice);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新公告
     */
    @PutMapping("/{id}")
    public Result<SystemNotice> updateNotice(@PathVariable Long id, @Valid @RequestBody SystemNotice notice) {
        try {
            SystemNotice updatedNotice = systemNoticeService.updateNotice(id, notice);
            return Result.success("公告更新成功", updatedNotice);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除公告
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteNotice(@PathVariable Long id) {
        try {
            systemNoticeService.deleteNotice(id);
            return Result.success("公告删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用公告
     */
    @PostMapping("/{id}/toggle-status")
    public Result<String> toggleNoticeStatus(@PathVariable Long id) {
        try {
            systemNoticeService.toggleNoticeStatus(id);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
