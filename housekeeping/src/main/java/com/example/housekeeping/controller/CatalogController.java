package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.HousekeepingService;
import com.example.housekeeping.domain.entity.ServiceCategory;
import com.example.housekeeping.dto.HousekeepingServiceRequest;
import com.example.housekeeping.dto.ServiceCategoryRequest;
import com.example.housekeeping.service.HousekeepingCatalogService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final HousekeepingCatalogService catalogService;

    public CatalogController(HousekeepingCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public List<ServiceCategory> listCategories() {
        return catalogService.listCategories();
    }

    @PostMapping("/categories")
    public ServiceCategory createCategory(@Valid @RequestBody ServiceCategoryRequest request) {
        return catalogService.createCategory(request);
    }

    @PutMapping("/categories/{id}")
    public ServiceCategory updateCategory(@PathVariable Long id, @Valid @RequestBody ServiceCategoryRequest request) {
        return catalogService.updateCategory(id, request);
    }

    @DeleteMapping("/categories/{id}")
    public void deleteCategory(@PathVariable Long id) {
        catalogService.deleteCategory(id);
    }

    @GetMapping("/services")
    public List<HousekeepingService> listServices() {
        return catalogService.listServices();
    }

    @GetMapping("/categories/{categoryId}/services")
    public List<HousekeepingService> listServicesByCategory(@PathVariable Long categoryId) {
        return catalogService.listServicesByCategory(categoryId);
    }

    @GetMapping("/services/featured")
    public List<HousekeepingService> listFeaturedServices() {
        return catalogService.listFeaturedServices();
    }

    @PostMapping("/services")
    public HousekeepingService createService(@Valid @RequestBody HousekeepingServiceRequest request) {
        return catalogService.createService(request);
    }

    @PutMapping("/services/{id}")
    public HousekeepingService updateService(@PathVariable Long id, @Valid @RequestBody HousekeepingServiceRequest request) {
        return catalogService.updateService(id, request);
    }

    @DeleteMapping("/services/{id}")
    public void deleteService(@PathVariable Long id) {
        catalogService.deleteService(id);
    }
}
