package com.example.housekeeping.service;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务分类管理服务类
 */
@Service
public class ServiceCategoryService {
    
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
    
    /**
     * 分页查询分类
     */
    public PageResult<ServiceCategory> getCategories(int page, int size, String name, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "sortOrder"));
        Page<ServiceCategory> categoryPage = serviceCategoryRepository.findByConditions(name, status, pageable);
        return PageResult.of(categoryPage);
    }
    
    /**
     * 获取所有启用的分类
     */
    public List<ServiceCategory> getActiveCategories() {
        return serviceCategoryRepository.findByStatusOrderBySortOrderAsc(1);
    }
    
    /**
     * 根据ID获取分类
     */
    public ServiceCategory getCategoryById(Long id) {
        return serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }
    
    /**
     * 创建分类
     */
    public ServiceCategory createCategory(ServiceCategory category) {
        // 检查分类名称是否已存在
        if (serviceCategoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("分类名称已存在");
        }
        
        return serviceCategoryRepository.save(category);
    }
    
    /**
     * 更新分类
     */
    public ServiceCategory updateCategory(Long id, ServiceCategory category) {
        ServiceCategory existingCategory = getCategoryById(id);
        
        // 检查分类名称是否已被其他分类使用
        if (serviceCategoryRepository.existsByNameAndIdNot(category.getName(), id)) {
            throw new RuntimeException("分类名称已存在");
        }
        
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        existingCategory.setIcon(category.getIcon());
        existingCategory.setSortOrder(category.getSortOrder());
        existingCategory.setStatus(category.getStatus());
        
        return serviceCategoryRepository.save(existingCategory);
    }
    
    /**
     * 删除分类
     */
    public void deleteCategory(Long id) {
        if (!serviceCategoryRepository.existsById(id)) {
            throw new RuntimeException("分类不存在");
        }
        serviceCategoryRepository.deleteById(id);
    }
    
    /**
     * 启用/禁用分类
     */
    public void toggleCategoryStatus(Long id) {
        ServiceCategory category = getCategoryById(id);
        category.setStatus(category.getStatus() == 1 ? 0 : 1);
        serviceCategoryRepository.save(category);
    }
}
