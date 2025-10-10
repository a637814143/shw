package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.BannerRequest;
import com.example.housekeeping.entity.Banner;
import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.repository.BannerRepository;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
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
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerRepository bannerRepository;
    private final HousekeepingServiceRepository serviceRepository;

    @GetMapping
    public ApiResponse<Page<Banner>> list(Pageable pageable) {
        return ApiResponse.success(bannerRepository.findAll(pageable));
    }

    @PostMapping
    public ApiResponse<Banner> create(@RequestBody @Valid BannerRequest request) {
        return ApiResponse.success(bannerRepository.save(map(new Banner(), request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<Banner> update(@PathVariable Long id, @RequestBody @Valid BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new BusinessException("轮播图不存在"));
        return ApiResponse.success(bannerRepository.save(map(banner, request)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        bannerRepository.deleteById(id);
        return ApiResponse.success();
    }

    private Banner map(Banner banner, BannerRequest request) {
        banner.setTitle(request.getTitle());
        banner.setImageUrl(request.getImageUrl());
        banner.setLinkUrl(request.getLinkUrl());
        banner.setSortOrder(request.getSortOrder());
        banner.setStatus(request.getStatus());
        if (request.getServiceId() != null) {
            HousekeepingService service = serviceRepository.findById(request.getServiceId())
                    .orElseThrow(() -> new BusinessException("服务不存在"));
            banner.setService(service);
        } else {
            banner.setService(null);
        }
        return banner;
    }
}
