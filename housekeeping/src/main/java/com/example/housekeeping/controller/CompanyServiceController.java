package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.CompanyServicePageResponse;
import com.example.housekeeping.dto.HousekeepServiceRequest;
import com.example.housekeeping.dto.HousekeepServiceResponse;
import com.example.housekeeping.dto.IdListRequest;
import com.example.housekeeping.dto.OrderProgressUpdateRequest;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.dto.ServiceReviewResponse;
import com.example.housekeeping.service.HousekeepServiceManager;
import com.example.housekeeping.service.ServiceOrderService;
import com.example.housekeeping.service.ServiceReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家政公司管理自身服务的接口。
 */
@RestController
@RequestMapping("/api/company/services")
public class CompanyServiceController {

    @Autowired
    private HousekeepServiceManager housekeepServiceManager;

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private ServiceReviewService serviceReviewService;

    @GetMapping
    public Result<CompanyServicePageResponse> listCompanyServices(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "categoryId", required = false) Long categoryId,
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "10") Integer size
    ) {
        return Result.success(
            housekeepServiceManager.listForCurrentCompany(
                keyword,
                categoryId,
                page == null ? 1 : page,
                size == null ? 10 : size
            )
        );
    }

    @PostMapping
    public Result<HousekeepServiceResponse> createService(@Valid @RequestBody HousekeepServiceRequest request) {
        return Result.success("创建成功", housekeepServiceManager.createService(request));
    }

    @PutMapping("/{serviceId}")
    public Result<HousekeepServiceResponse> updateService(@PathVariable Long serviceId,
                                                          @Valid @RequestBody HousekeepServiceRequest request) {
        return Result.success("更新成功", housekeepServiceManager.updateService(serviceId, request));
    }

    @DeleteMapping("/{serviceId}")
    public Result<Void> deleteService(@PathVariable Long serviceId) {
        housekeepServiceManager.deleteService(serviceId);
        return Result.success("删除成功", null);
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteServices(@Valid @RequestBody IdListRequest request) {
        housekeepServiceManager.deleteServices(request.getIds());
        return Result.success("删除成功", null);
    }

    @GetMapping("/orders")
    public Result<List<ServiceOrderResponse>> listActiveOrders(
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return Result.success(serviceOrderService.listActiveOrdersForCompany(keyword));
    }

    @DeleteMapping("/orders/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long orderId) {
        serviceOrderService.deleteOrdersForCompany(List.of(orderId));
        return Result.success("删除成功", null);
    }

    @DeleteMapping("/orders/batch")
    public Result<Void> deleteOrders(@Valid @RequestBody IdListRequest request) {
        serviceOrderService.deleteOrdersForCompany(request.getIds());
        return Result.success("删除成功", null);
    }

    @PostMapping("/orders/{orderId}/progress")
    public Result<ServiceOrderResponse> updateOrderProgress(@PathVariable Long orderId,
                                                            @Valid @RequestBody OrderProgressUpdateRequest request) {
        return Result.success("进度已更新", serviceOrderService.updateOrderProgress(orderId, request));
    }

    @GetMapping("/reviews")
    public Result<List<ServiceReviewResponse>> listServiceReviews(
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return Result.success(serviceReviewService.listReviewsForCurrentCompany(keyword));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public Result<Void> deleteReview(@PathVariable Long reviewId) {
        serviceReviewService.deleteReviewsForCurrentCompany(List.of(reviewId));
        return Result.success("删除成功", null);
    }

    @DeleteMapping("/reviews/batch")
    public Result<Void> deleteReviews(@Valid @RequestBody IdListRequest request) {
        serviceReviewService.deleteReviewsForCurrentCompany(request.getIds());
        return Result.success("删除成功", null);
    }
}
