package com.example.housekeeping.service.impl;

import com.example.housekeeping.domain.entity.HousekeepingService;
import com.example.housekeeping.domain.entity.ServiceCategory;
import com.example.housekeeping.dto.HousekeepingServiceRequest;
import com.example.housekeeping.dto.ServiceCategoryRequest;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import com.example.housekeeping.service.HousekeepingCatalogService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class HousekeepingCatalogServiceImpl implements HousekeepingCatalogService {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final HousekeepingServiceRepository housekeepingServiceRepository;

    @Override
    public List<ServiceCategory> listCategories() {
        return serviceCategoryRepository.findAll();
    }

    @Override
    public ServiceCategory createCategory(ServiceCategoryRequest request) {
        ServiceCategory category = new ServiceCategory();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setIconUrl(request.iconUrl());
        category.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        category.setEnabled(request.enabled());
        return serviceCategoryRepository.save(category);
    }

    @Override
    public ServiceCategory updateCategory(Long id, ServiceCategoryRequest request) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "分类不存在"));
        category.setName(request.name());
        category.setDescription(request.description());
        category.setIconUrl(request.iconUrl());
        category.setSortOrder(request.sortOrder() != null ? request.sortOrder() : category.getSortOrder());
        category.setEnabled(request.enabled());
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        if (!serviceCategoryRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "分类不存在");
        }
        serviceCategoryRepository.deleteById(id);
    }

    @Override
    public List<HousekeepingService> listServices() {
        return housekeepingServiceRepository.findAll();
    }

    @Override
    public List<HousekeepingService> listServicesByCategory(Long categoryId) {
        return housekeepingServiceRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<HousekeepingService> listFeaturedServices() {
        return housekeepingServiceRepository.findByFeaturedTrueOrderByOrderCountDesc();
    }

    @Override
    public HousekeepingService createService(HousekeepingServiceRequest request) {
        ServiceCategory category = serviceCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "分类不存在"));
        HousekeepingService service = new HousekeepingService();
        service.setCategory(category);
        service.setTitle(request.title());
        service.setDescription(request.description());
        service.setPrice(request.price());
        service.setDurationMinutes(request.durationMinutes());
        service.setFeatured(request.featured());
        service.setCoverImageUrl(request.coverImageUrl());
        service.setTags(request.tags());
        service.setEnabled(request.enabled());
        return housekeepingServiceRepository.save(service);
    }

    @Override
    public HousekeepingService updateService(Long id, HousekeepingServiceRequest request) {
        HousekeepingService service = housekeepingServiceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "服务不存在"));
        ServiceCategory category = serviceCategoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "分类不存在"));
        service.setCategory(category);
        service.setTitle(request.title());
        service.setDescription(request.description());
        service.setPrice(request.price());
        service.setDurationMinutes(request.durationMinutes());
        service.setFeatured(request.featured());
        service.setCoverImageUrl(request.coverImageUrl());
        service.setTags(request.tags());
        service.setEnabled(request.enabled());
        return service;
    }

    @Override
    public void deleteService(Long id) {
        if (!housekeepingServiceRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "服务不存在");
        }
        housekeepingServiceRepository.deleteById(id);
    }
}
