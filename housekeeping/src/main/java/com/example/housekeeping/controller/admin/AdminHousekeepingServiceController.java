package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.ServiceRequest;
import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
public class AdminHousekeepingServiceController {

    private final HousekeepingServiceRepository serviceRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final ServiceProviderRepository providerRepository;

    @GetMapping
    public ApiResponse<Page<HousekeepingService>> list(@RequestParam(required = false) String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isBlank()) {
            return ApiResponse.success(serviceRepository.findByNameContainingIgnoreCase(keyword, pageable));
        }
        return ApiResponse.success(serviceRepository.findAll(pageable));
    }

    @PostMapping
    public ApiResponse<HousekeepingService> create(@RequestBody @Valid ServiceRequest request) {
        return ApiResponse.success(serviceRepository.save(mapToEntity(new HousekeepingService(), request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<HousekeepingService> update(@PathVariable Long id, @RequestBody @Valid ServiceRequest request) {
        HousekeepingService service = serviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务不存在"));
        return ApiResponse.success(serviceRepository.save(mapToEntity(service, request)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        serviceRepository.deleteById(id);
        return ApiResponse.success();
    }

    private HousekeepingService mapToEntity(HousekeepingService target, ServiceRequest request) {
        ServiceCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BusinessException("分类不存在"));
        ServiceProvider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        target.setName(request.getName());
        target.setDescription(request.getDescription());
        target.setContent(request.getContent());
        target.setCategory(category);
        target.setProvider(provider);
        target.setPrice(request.getPrice());
        target.setUnit(request.getUnit());
        target.setDuration(request.getDuration());
        target.setImages(request.getImages());
        target.setTags(request.getTags());
        target.setStatus(request.getStatus());
        return target;
    }
}
