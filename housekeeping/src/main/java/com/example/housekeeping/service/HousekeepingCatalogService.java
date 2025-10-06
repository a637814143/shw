package com.example.housekeeping.service;

import com.example.housekeeping.domain.entity.HousekeepingService;
import com.example.housekeeping.domain.entity.ServiceCategory;
import com.example.housekeeping.dto.HousekeepingServiceRequest;
import com.example.housekeeping.dto.ServiceCategoryRequest;
import java.util.List;

public interface HousekeepingCatalogService {
    List<ServiceCategory> listCategories();

    ServiceCategory createCategory(ServiceCategoryRequest request);

    ServiceCategory updateCategory(Long id, ServiceCategoryRequest request);

    void deleteCategory(Long id);

    List<HousekeepingService> listServices();

    List<HousekeepingService> listServicesByCategory(Long categoryId);

    List<HousekeepingService> listFeaturedServices();

    HousekeepingService createService(HousekeepingServiceRequest request);

    HousekeepingService updateService(Long id, HousekeepingServiceRequest request);

    void deleteService(Long id);
}
