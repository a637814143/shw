package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.HousekeepServiceResponse;
import com.example.housekeeping.dto.RefundRequest;
import com.example.housekeeping.dto.ServiceOrderRequest;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.dto.ServiceReviewRequest;
import com.example.housekeeping.dto.ServiceReviewResponse;
import com.example.housekeeping.service.HousekeepServiceManager;
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

    @GetMapping
    public Result<List<HousekeepServiceResponse>> listServices() {
        return Result.success(housekeepServiceManager.listAllServices());
    }

    @GetMapping("/orders")
    public Result<List<ServiceOrderResponse>> listOrders() {
        return Result.success(serviceOrderService.listOrdersForCurrentUser());
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

    @PostMapping("/reviews")
    public Result<ServiceReviewResponse> createReview(@Valid @RequestBody ServiceReviewRequest request) {
        return Result.success("评价已提交", serviceReviewService.createReview(request));
    }
}
