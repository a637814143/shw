package com.example.housekeeping.service;

import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 服务分类服务
 */
@Service
public class ServiceCategoryService {

    private static final Logger log = LoggerFactory.getLogger(ServiceCategoryService.class);
    private final ServiceCategoryRepository serviceCategoryRepository;

    public ServiceCategoryService(ServiceCategoryRepository serviceCategoryRepository) {
        this.serviceCategoryRepository = serviceCategoryRepository;
    }

    /**
     * 根据ID查找分类
     */
    public Optional<ServiceCategory> findById(Long id) {
        return serviceCategoryRepository.findById(id);
    }

    /**
     * 根据名称查找分类
     */
    public ServiceCategory findByName(String name) {
        return serviceCategoryRepository.findByName(name);
    }

    /**
     * 保存分类
     */
    public ServiceCategory save(ServiceCategory category) {
        return serviceCategoryRepository.save(category);
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
    public ServiceCategory updateCategory(Long categoryId, ServiceCategory updateCategory) {
        ServiceCategory category = serviceCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("分类不存在"));

        // 检查新名称是否与其他分类冲突
        if (!category.getName().equals(updateCategory.getName()) && 
            serviceCategoryRepository.existsByName(updateCategory.getName())) {
            throw new RuntimeException("分类名称已存在");
        }

        if (updateCategory.getName() != null) {
            category.setName(updateCategory.getName());
        }
        if (updateCategory.getDescription() != null) {
            category.setDescription(updateCategory.getDescription());
        }
        if (updateCategory.getIcon() != null) {
            category.setIcon(updateCategory.getIcon());
        }
        if (updateCategory.getSortOrder() != null) {
            category.setSortOrder(updateCategory.getSortOrder());
        }
        if (updateCategory.getStatus() != null) {
            category.setStatus(updateCategory.getStatus());
        }

        return serviceCategoryRepository.save(category);
    }

    /**
     * 查询所有分类
     */
    public List<ServiceCategory> findAll() {
        return serviceCategoryRepository.findAll();
    }

    /**
     * 查询所有启用的分类
     */
    public List<ServiceCategory> findAllEnabled() {
        return serviceCategoryRepository.findAllEnabledOrderBySortOrder();
    }

    /**
     * 根据状态查询分类
     */
    public List<ServiceCategory> findByStatus(Integer status) {
        return serviceCategoryRepository.findByStatusOrderBySortOrder(status);
    }

    /**
     * 启用/禁用分类
     */
    public void updateCategoryStatus(Long categoryId, Integer status) {
        ServiceCategory category = serviceCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("分类不存在"));
        
        category.setStatus(status);
        serviceCategoryRepository.save(category);
    }

    /**
     * 删除分类
     */
    public void deleteCategory(Long categoryId) {
        if (!serviceCategoryRepository.existsById(categoryId)) {
            throw new RuntimeException("分类不存在");
        }
        serviceCategoryRepository.deleteById(categoryId);
    }

    /**
     * 统计分类数量
     */
    public long countCategories() {
        return serviceCategoryRepository.count();
    }
}
