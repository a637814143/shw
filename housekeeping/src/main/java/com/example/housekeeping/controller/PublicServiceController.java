package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.HousekeepServiceResponse;
import com.example.housekeeping.dto.ServiceReviewResponse;
import com.example.housekeeping.service.HousekeepServiceManager;
import com.example.housekeeping.service.ServiceReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 向所有用户公开的服务列表与评价接口。
 */
@RestController
@RequestMapping("/api/public/services")
public class PublicServiceController {

    @Autowired
    private HousekeepServiceManager housekeepServiceManager;

    @Autowired
    private ServiceReviewService serviceReviewService;

    @GetMapping
    public Result<List<HousekeepServiceResponse>> listServices(@RequestParam(value = "keyword", required = false)
                                                               String keyword,
                                                               @RequestParam(value = "categoryId", required = false)
                                                               Long categoryId) {
        return Result.success(housekeepServiceManager.listAllServices(keyword, categoryId));
    }

    @GetMapping("/{serviceId}/reviews")
    public Result<List<ServiceReviewResponse>> listReviews(@PathVariable Long serviceId) {
        return Result.success(serviceReviewService.listReviewsForService(serviceId));
    }
}
