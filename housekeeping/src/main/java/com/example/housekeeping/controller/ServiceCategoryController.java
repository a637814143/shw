package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.service.ServiceCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 服务分类控制器
 */
@RestController
@RequestMapping("/api/category")
public class ServiceCategoryController {

    private static final Logger log = LoggerFactory.getLogger(ServiceCategoryController.class);
    private final ServiceCategoryService serviceCategoryService;

    public ServiceCategoryController(ServiceCategoryService serviceCategoryService) {
        this.serviceCategoryService = serviceCategoryService;
    }

    /**
     * 获取所有启用的分类（公开接口）
     */
    @GetMapping("/public/list")
    public Result<List<ServiceCategory>> getPublicCategories() {
        List<ServiceCategory> categories = serviceCategoryService.findAllEnabled();
        return Result.success(categories);
    }

    /**
     * 获取所有分类（管理员）
     */
    @GetMapping("/list")
    public Result<List<ServiceCategory>> getAllCategories() {
        List<ServiceCategory> categories = serviceCategoryService.findAll();
        return Result.success(categories);
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public Result<ServiceCategory> getCategoryById(@PathVariable Long id) {
        ServiceCategory category = serviceCategoryService.findById(id)
            .orElseThrow(() -> new RuntimeException("分类不存在"));
        return Result.success(category);
    }

    /**
     * 创建分类（管理员）
     */
    @PostMapping
    public Result<ServiceCategory> createCategory(@RequestBody @Valid ServiceCategory category) {
        ServiceCategory savedCategory = serviceCategoryService.createCategory(category);
        return Result.success("创建成功", savedCategory);
    }

    /**
     * 更新分类（管理员）
     */
    @PutMapping("/{id}")
    public Result<ServiceCategory> updateCategory(@PathVariable Long id, @RequestBody @Valid ServiceCategory updateCategory) {
        ServiceCategory category = serviceCategoryService.updateCategory(id, updateCategory);
        return Result.success("更新成功", category);
    }

    /**
     * 更新分类状态（管理员）
     */
    @PutMapping("/{id}/status")
    public Result<String> updateCategoryStatus(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        Integer status = request.get("status");
        if (status == null) {
            return Result.badRequest("状态不能为空");
        }
        
        serviceCategoryService.updateCategoryStatus(id, status);
        return Result.success("状态更新成功");
    }

    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        serviceCategoryService.deleteCategory(id);
        return Result.success("分类删除成功");
    }

    /**
     * 获取分类统计信息（管理员）
     */
    @GetMapping("/statistics")
    public Result<Map<String, Long>> getCategoryStatistics() {
        Map<String, Long> statistics = Map.of(
            "totalCategories", serviceCategoryService.countCategories()
        );
        return Result.success(statistics);
    }
}
