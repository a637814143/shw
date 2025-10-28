package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.IdListRequest;
import com.example.housekeeping.dto.ServiceCategoryRequest;
import com.example.housekeeping.dto.ServiceCategoryResponse;
import com.example.housekeeping.service.ServiceCategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/service-categories")
public class AdminServiceCategoryController {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    @GetMapping
    public Result<List<ServiceCategoryResponse>> listCategories() {
        return Result.success(serviceCategoryService.listForAdmin());
    }

    @PostMapping
    public Result<ServiceCategoryResponse> createCategory(@Valid @RequestBody ServiceCategoryRequest request) {
        return Result.success("创建成功", serviceCategoryService.create(request));
    }

    @PutMapping("/{id}")
    public Result<ServiceCategoryResponse> updateCategory(@PathVariable Long id,
                                                          @Valid @RequestBody ServiceCategoryRequest request) {
        return Result.success("更新成功", serviceCategoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        serviceCategoryService.delete(id);
        return Result.success(null);
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteCategories(@Valid @RequestBody IdListRequest request) {
        serviceCategoryService.delete(request.getIds());
        return Result.success(null);
    }
}
