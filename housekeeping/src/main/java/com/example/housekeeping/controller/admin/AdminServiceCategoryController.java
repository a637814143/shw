package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminServiceCategoryController {

    private final ServiceCategoryRepository categoryRepository;

    @GetMapping
    public ApiResponse<Page<ServiceCategory>> list(Pageable pageable) {
        return ApiResponse.success(categoryRepository.findAll(pageable));
    }

    @PostMapping
    public ApiResponse<ServiceCategory> create(@RequestBody @Valid ServiceCategory category) {
        return ApiResponse.success(categoryRepository.save(category));
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceCategory> update(@PathVariable Long id, @RequestBody @Valid ServiceCategory category) {
        ServiceCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        existing.setIcon(category.getIcon());
        existing.setSortOrder(category.getSortOrder());
        existing.setStatus(category.getStatus());
        return ApiResponse.success(categoryRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ApiResponse.success();
    }
}
