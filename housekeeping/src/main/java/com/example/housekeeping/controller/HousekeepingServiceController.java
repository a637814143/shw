package com.example.housekeeping.controller;

import com.example.housekeeping.dto.HousekeepingServiceRequest;
import com.example.housekeeping.entity.HousekeepingServiceItem;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.exception.ResourceNotFoundException;
import com.example.housekeeping.repository.HousekeepingServiceItemRepository;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/services")
public class HousekeepingServiceController {

    private final HousekeepingServiceItemRepository serviceRepository;
    private final ServiceCategoryRepository categoryRepository;

    public HousekeepingServiceController(HousekeepingServiceItemRepository serviceRepository,
                                         ServiceCategoryRepository categoryRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public Page<HousekeepingServiceItem> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Long categoryId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("id").ascending());
        String normalizedKeyword = keyword != null && !keyword.isBlank() ? keyword : null;
        return serviceRepository.search(normalizedKeyword, categoryId, pageable);
    }

    @GetMapping("/{id}")
    public HousekeepingServiceItem detail(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的家政服务"));
    }

    @PostMapping
    public ResponseEntity<HousekeepingServiceItem> create(@Valid @RequestBody HousekeepingServiceRequest request) {
        ServiceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的服务分类"));
        HousekeepingServiceItem item = HousekeepingServiceItem.builder()
                .name(request.getName())
                .price(request.getPrice())
                .unit(request.getUnit())
                .sales(request.getSales())
                .popularity(request.getPopularity())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();
        HousekeepingServiceItem saved = serviceRepository.save(item);
        return ResponseEntity.created(URI.create("/api/services/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public HousekeepingServiceItem update(@PathVariable Long id, @Valid @RequestBody HousekeepingServiceRequest request) {
        HousekeepingServiceItem item = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的家政服务"));
        ServiceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的服务分类"));
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setUnit(request.getUnit());
        item.setSales(request.getSales());
        item.setPopularity(request.getPopularity());
        item.setDescription(request.getDescription());
        item.setImageUrl(request.getImageUrl());
        item.setCategory(category);
        return serviceRepository.save(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        HousekeepingServiceItem item = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的家政服务"));
        serviceRepository.delete(item);
        return ResponseEntity.noContent().build();
    }
}
