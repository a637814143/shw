package com.example.housekeeping.controller;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.common.Result;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.service.ServiceCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务分类管理控制器
 */
@RestController
@RequestMapping("/api/admin/categories")
public class ServiceCategoryController {
    
    @Autowired
    private ServiceCategoryService serviceCategoryService;
    
    /**
     * 分页查询分类
     */
    @GetMapping
    public Result<PageResult<ServiceCategory>> getCategories(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status) {
        try {
            PageResult<ServiceCategory> result = serviceCategoryService.getCategories(page, size, name, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有启用的分类
     */
    @GetMapping("/active")
    public Result<List<ServiceCategory>> getActiveCategories() {
        try {
            List<ServiceCategory> categories = serviceCategoryService.getActiveCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public Result<ServiceCategory> getCategoryById(@PathVariable Long id) {
        try {
            ServiceCategory category = serviceCategoryService.getCategoryById(id);
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 创建分类
     */
    @PostMapping
    public Result<ServiceCategory> createCategory(@Valid @RequestBody ServiceCategory category) {
        try {
            ServiceCategory createdCategory = serviceCategoryService.createCategory(category);
            return Result.success("分类创建成功", createdCategory);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<ServiceCategory> updateCategory(@PathVariable Long id, @Valid @RequestBody ServiceCategory category) {
        try {
            ServiceCategory updatedCategory = serviceCategoryService.updateCategory(id, category);
            return Result.success("分类更新成功", updatedCategory);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        try {
            serviceCategoryService.deleteCategory(id);
            return Result.success("分类删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用分类
     */
    @PostMapping("/{id}/toggle-status")
    public Result<String> toggleCategoryStatus(@PathVariable Long id) {
        try {
            serviceCategoryService.toggleCategoryStatus(id);
            return Result.success("状态更新成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
