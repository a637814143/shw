package com.example.housekeeping.controller;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.entity.Banner;
import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.entity.HousekeepingTip;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.SystemNotice;
import com.example.housekeeping.repository.BannerRepository;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.HousekeepingTipRepository;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.SystemNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicContentController {

    private final SystemNoticeRepository noticeRepository;
    private final BannerRepository bannerRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final HousekeepingServiceRepository serviceRepository;
    private final ServiceProviderRepository providerRepository;
    private final HousekeepingTipRepository tipRepository;

    @GetMapping("/notices")
    public ApiResponse<Page<SystemNotice>> notices(Pageable pageable) {
        return ApiResponse.success(noticeRepository.findAll(pageable));
    }

    @GetMapping("/banners")
    public ApiResponse<Page<Banner>> banners(Pageable pageable) {
        return ApiResponse.success(bannerRepository.findAll(pageable));
    }

    @GetMapping("/categories")
    public ApiResponse<Page<ServiceCategory>> categories(Pageable pageable) {
        return ApiResponse.success(categoryRepository.findAll(pageable));
    }

    @GetMapping("/services")
    public ApiResponse<Page<HousekeepingService>> services(@RequestParam(required = false) Long categoryId,
                                                           @RequestParam(required = false) Long providerId,
                                                           @RequestParam(required = false) String keyword,
                                                           Pageable pageable) {
        if (categoryId != null) {
            ServiceCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException("分类不存在"));
            return ApiResponse.success(serviceRepository.findByCategory(category, pageable));
        }
        if (providerId != null) {
            ServiceProvider provider = providerRepository.findById(providerId)
                    .orElseThrow(() -> new BusinessException("服务者不存在"));
            return ApiResponse.success(serviceRepository.findByProvider(provider, pageable));
        }
        if (keyword != null && !keyword.isBlank()) {
            return ApiResponse.success(serviceRepository.findByNameContainingIgnoreCase(keyword, pageable));
        }
        return ApiResponse.success(serviceRepository.findAll(pageable));
    }

    @GetMapping("/services/{id}")
    public ApiResponse<HousekeepingService> serviceDetail(@PathVariable Long id) {
        return ApiResponse.success(serviceRepository.findById(id)
                .orElseThrow(() -> new BusinessException("服务不存在")));
    }

    @GetMapping("/tips")
    public ApiResponse<Page<HousekeepingTip>> tips(Pageable pageable) {
        return ApiResponse.success(tipRepository.findAll(pageable));
    }

    @GetMapping("/tips/{id}")
    public ApiResponse<HousekeepingTip> tipDetail(@PathVariable Long id) {
        HousekeepingTip tip = tipRepository.findById(id)
                .orElseThrow(() -> new BusinessException("贴士不存在"));
        tip.setViewCount(tip.getViewCount() == null ? 1 : tip.getViewCount() + 1);
        tipRepository.save(tip);
        return ApiResponse.success(tip);
    }
}
