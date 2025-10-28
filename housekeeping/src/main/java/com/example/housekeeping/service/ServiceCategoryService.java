package com.example.housekeeping.service;

import com.example.housekeeping.dto.ServiceCategoryRequest;
import com.example.housekeeping.dto.ServiceCategoryResponse;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.CompanyStaffRepository;
import com.example.housekeeping.repository.HousekeepServiceRepository;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 服务分类管理。
 */
@Service
public class ServiceCategoryService {

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private HousekeepServiceRepository housekeepServiceRepository;

    @Autowired
    private CompanyStaffRepository companyStaffRepository;

    @Autowired
    private AccountLookupService accountLookupService;

    @Transactional(readOnly = true)
    public List<ServiceCategoryResponse> listAll() {
        return serviceCategoryRepository.findAllByOrderByNameAsc().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceCategoryResponse> listForAdmin() {
        ensureAdmin();
        return listAll();
    }

    @Transactional
    public ServiceCategoryResponse create(ServiceCategoryRequest request) {
        ensureAdmin();
        String name = normalizeName(request.getName());
        if (serviceCategoryRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new RuntimeException("分类名称已存在");
        }
        ServiceCategory category = new ServiceCategory();
        category.setName(name);
        category.setDescription(normalizeDescription(request.getDescription()));
        ServiceCategory saved = serviceCategoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Transactional
    public ServiceCategoryResponse update(Long id, ServiceCategoryRequest request) {
        ensureAdmin();
        ServiceCategory category = findCategory(id);
        String name = normalizeName(request.getName());
        serviceCategoryRepository.findByNameIgnoreCase(name)
            .filter(other -> !Objects.equals(other.getId(), category.getId()))
            .ifPresent(other -> {
                throw new RuntimeException("分类名称已存在");
            });
        category.setName(name);
        category.setDescription(normalizeDescription(request.getDescription()));
        ServiceCategory saved = serviceCategoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        ensureAdmin();
        ServiceCategory category = findCategory(id);
        ensureCategoryEmpty(category);
        serviceCategoryRepository.delete(category);
    }

    @Transactional
    public void delete(List<Long> ids) {
        ensureAdmin();
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<ServiceCategory> categories = ids.stream()
            .filter(Objects::nonNull)
            .distinct()
            .map(this::findCategory)
            .collect(Collectors.toList());
        for (ServiceCategory category : categories) {
            ensureCategoryEmpty(category);
        }
        serviceCategoryRepository.deleteAll(categories);
    }

    private void ensureCategoryEmpty(ServiceCategory category) {
        long serviceCount = housekeepServiceRepository.countByCategory(category);
        long staffCount = companyStaffRepository.countByCategory(category);
        if (serviceCount > 0 || staffCount > 0) {
            throw new RuntimeException("分类下仍有关联的服务或人员，无法删除");
        }
    }

    private ServiceCategory findCategory(Long id) {
        if (id == null) {
            throw new RuntimeException("请选择要操作的分类");
        }
        return serviceCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("分类不存在"));
    }

    private void ensureAdmin() {
        accountLookupService.ensureCurrentAccountRole(AccountRole.ADMIN);
    }

    private ServiceCategoryResponse mapToResponse(ServiceCategory category) {
        long serviceCount = housekeepServiceRepository.countByCategory(category);
        long totalStaff = companyStaffRepository.countByCategory(category);
        long availableStaff = companyStaffRepository.countByCategoryAndAssignedFalse(category);
        return new ServiceCategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            serviceCount,
            totalStaff,
            availableStaff
        );
    }

    private String normalizeName(String name) {
        if (name == null) {
            throw new RuntimeException("分类名称不能为空");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new RuntimeException("分类名称不能为空");
        }
        return trimmed;
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
