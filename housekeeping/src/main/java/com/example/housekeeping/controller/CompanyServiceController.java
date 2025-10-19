package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.AssignOrderStaffRequest;
import com.example.housekeeping.dto.CompanyStaffRequest;
import com.example.housekeeping.dto.CompanyStaffResponse;
import com.example.housekeeping.dto.HousekeepServiceRequest;
import com.example.housekeeping.dto.HousekeepServiceResponse;
import com.example.housekeeping.dto.OrderProgressUpdateRequest;
import com.example.housekeeping.dto.RefundDecisionRequest;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.dto.ServiceReviewResponse;
import com.example.housekeeping.service.HousekeepServiceManager;
import com.example.housekeeping.service.ServiceOrderService;
import com.example.housekeeping.service.ServiceReviewService;
import com.example.housekeeping.service.CompanyStaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家政公司管理自身服务及退款的接口。
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

    @Autowired
    private CompanyStaffService companyStaffService;

    @GetMapping
    public Result<List<HousekeepServiceResponse>> listCompanyServices() {
        return Result.success(housekeepServiceManager.listForCurrentCompany());
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

    @GetMapping("/refunds")
    public Result<List<ServiceOrderResponse>> listRefundRequests() {
        return Result.success(serviceOrderService.listRefundRequestsForCompany());
    }

    @GetMapping("/orders")
    public Result<List<ServiceOrderResponse>> listActiveOrders() {
        return Result.success(serviceOrderService.listActiveOrdersForCompany());
    }

    @PostMapping("/orders/{orderId}/assign")
    public Result<ServiceOrderResponse> assignOrder(@PathVariable Long orderId,
                                                    @Valid @RequestBody AssignOrderStaffRequest request) {
        return Result.success("分配成功", serviceOrderService.assignWorker(orderId, request));
    }

    @PostMapping("/refunds/{orderId}")
    public Result<ServiceOrderResponse> handleRefund(@PathVariable Long orderId,
                                                     @Valid @RequestBody RefundDecisionRequest request) {
        return Result.success("操作成功", serviceOrderService.handleRefund(orderId, request));
    }

    @PostMapping("/orders/{orderId}/progress")
    public Result<ServiceOrderResponse> updateOrderProgress(@PathVariable Long orderId,
                                                            @Valid @RequestBody OrderProgressUpdateRequest request) {
        return Result.success("进度已更新", serviceOrderService.updateOrderProgress(orderId, request));
    }

    @GetMapping("/staff")
    public Result<List<CompanyStaffResponse>> listStaff() {
        return Result.success(companyStaffService.listStaff());
    }

    @PostMapping("/staff")
    public Result<CompanyStaffResponse> createStaff(@Valid @RequestBody CompanyStaffRequest request) {
        return Result.success("已添加人员", companyStaffService.createStaff(request));
    }

    @PutMapping("/staff/{staffId}")
    public Result<CompanyStaffResponse> updateStaff(@PathVariable Long staffId,
                                                    @Valid @RequestBody CompanyStaffRequest request) {
        return Result.success("已更新人员", companyStaffService.updateStaff(staffId, request));
    }

    @DeleteMapping("/staff/{staffId}")
    public Result<Void> deleteStaff(@PathVariable Long staffId) {
        companyStaffService.deleteStaff(staffId);
        return Result.success("已删除人员", null);
    }

    @GetMapping("/reviews")
    public Result<List<ServiceReviewResponse>> listServiceReviews() {
        return Result.success(serviceReviewService.listReviewsForCurrentCompany());
    }
}
