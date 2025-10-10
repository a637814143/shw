package com.example.housekeeping.controller.provider;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.ServiceReview;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.ServiceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider/reviews")
@RequiredArgsConstructor
public class ProviderReviewController {

    private final ServiceReviewRepository reviewRepository;
    private final ServiceProviderRepository providerRepository;

    @GetMapping
    public ApiResponse<Page<ServiceReview>> list(@RequestParam Long providerId, Pageable pageable) {
        ServiceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        return ApiResponse.success(reviewRepository.findByProvider(provider, pageable));
    }
}
