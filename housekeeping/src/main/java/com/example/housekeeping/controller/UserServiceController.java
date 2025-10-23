package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.HousekeepServiceResponse;
import com.example.housekeeping.dto.IdListRequest;
import com.example.housekeeping.dto.PageResponse;
import com.example.housekeeping.dto.RefundRequest;
import com.example.housekeeping.dto.ServiceFavoriteResponse;
import com.example.housekeeping.dto.ServiceOrderRequest;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.dto.ServiceReviewRequest;
import com.example.housekeeping.dto.ServiceReviewResponse;
import com.example.housekeeping.dto.UserOrderPageResponse;
import com.example.housekeeping.service.HousekeepServiceManager;
import com.example.housekeeping.service.ServiceFavoriteService;
import com.example.housekeeping.service.ServiceOrderService;
import com.example.housekeeping.service.ServiceReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 普通用户的服务选择、评价与退款相关接口。
 */
@RestController
@RequestMapping("/api/user/services")
public class UserServiceController {

    @Autowired
    private HousekeepServiceManager housekeepServiceManager;

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private ServiceReviewService serviceReviewService;

    @Autowired
    private ServiceFavoriteService serviceFavoriteService;

    @GetMapping
    public Result<List<HousekeepServiceResponse>> listServices(@RequestParam(value = "keyword", required = false)
                                                               String keyword) {
        return Result.success(housekeepServiceManager.listAllServices(keyword));
    }

    @GetMapping("/orders")
    public Result<UserOrderPageResponse> listOrders(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {
        int safePage = page == null ? 1 : page;
        int safeSize = size == null ? 5 : size;
        return Result.success(serviceOrderService.listOrdersForCurrentUser(keyword, safePage, safeSize));
    }

    @GetMapping("/favorites")
    public Result<List<ServiceFavoriteResponse>> listFavorites() {
        return Result.success(serviceFavoriteService.listFavorites());
    }

    @PostMapping("/orders")
    public Result<ServiceOrderResponse> createOrder(@Valid @RequestBody ServiceOrderRequest request) {
        return Result.success("下单成功", serviceOrderService.createOrder(request));
    }

    @PostMapping("/orders/{orderId}/refund")
    public Result<ServiceOrderResponse> requestRefund(@PathVariable Long orderId,
                                                      @Valid @RequestBody RefundRequest request) {
        return Result.success("退款申请已提交", serviceOrderService.requestRefund(orderId, request));
    }

    @DeleteMapping("/orders/{orderId}")
    public Result<Void> deleteOrder(@PathVariable Long orderId) {
        serviceOrderService.deleteOrdersForCurrentUser(List.of(orderId));
        return Result.success("删除成功", null);
    }

    @DeleteMapping("/orders/batch")
    public Result<Void> deleteOrders(@Valid @RequestBody IdListRequest request) {
        serviceOrderService.deleteOrdersForCurrentUser(request.getIds());
        return Result.success("删除成功", null);
    }

    @PostMapping("/{serviceId}/favorite")
    public Result<Void> addFavorite(@PathVariable Long serviceId) {
        serviceFavoriteService.addFavorite(serviceId);
        return Result.success("收藏成功", null);
    }

    @DeleteMapping("/{serviceId}/favorite")
    public Result<Void> removeFavorite(@PathVariable Long serviceId) {
        serviceFavoriteService.removeFavorite(serviceId);
        return Result.success("已取消收藏", null);
    }

    @PostMapping("/reviews")
    public Result<ServiceReviewResponse> createReview(@Valid @RequestBody ServiceReviewRequest request) {
        return Result.success("评价已提交", serviceReviewService.createReview(request));
    }

    @GetMapping("/reviews")
    public Result<PageResponse<ServiceReviewResponse>> listReviews(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "size", defaultValue = "5") Integer size
    ) {
        int safePage = page == null ? 1 : page;
        int safeSize = size == null ? 5 : size;
        return Result.success(serviceReviewService.listReviewsForCurrentUser(keyword, safePage, safeSize));
    }

    @DeleteMapping("/reviews/{reviewId}")
    public Result<Void> deleteReview(@PathVariable Long reviewId) {
        serviceReviewService.deleteReviewsForCurrentUser(List.of(reviewId));
        return Result.success("删除成功", null);
    }

    @DeleteMapping("/reviews/batch")
    public Result<Void> deleteReviews(@Valid @RequestBody IdListRequest request) {
        serviceReviewService.deleteReviewsForCurrentUser(request.getIds());
        return Result.success("删除成功", null);
    }
}
