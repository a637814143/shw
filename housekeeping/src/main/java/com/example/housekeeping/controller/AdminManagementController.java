package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.RefundDecisionRequest;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.dto.UpdateLoyaltyRequest;
import com.example.housekeeping.dto.UpdatePasswordRequest;
import com.example.housekeeping.dto.UpdateWalletRequest;
import com.example.housekeeping.dto.UserAccountResponse;
import com.example.housekeeping.service.AdminAccountService;
import com.example.housekeeping.service.ServiceOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员端接口。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminManagementController {

    @Autowired
    private AdminAccountService adminAccountService;

    @Autowired
    private ServiceOrderService serviceOrderService;

    @GetMapping("/users")
    public Result<List<UserAccountResponse>> listUsers() {
        return Result.success(adminAccountService.listUsers());
    }

    @PutMapping("/users/{userId}/wallet")
    public Result<UserAccountResponse> updateWallet(@PathVariable Long userId,
                                                    @Valid @RequestBody UpdateWalletRequest request) {
        return Result.success("余额已更新", adminAccountService.updateWallet(userId, request));
    }

    @PutMapping("/users/{userId}/password")
    public Result<UserAccountResponse> updatePassword(@PathVariable Long userId,
                                                      @Valid @RequestBody UpdatePasswordRequest request) {
        return Result.success("密码已重置", adminAccountService.updatePassword(userId, request));
    }

    @PutMapping("/users/{userId}/loyalty")
    public Result<UserAccountResponse> updateLoyalty(@PathVariable Long userId,
                                                     @Valid @RequestBody UpdateLoyaltyRequest request) {
        return Result.success("积分已更新", adminAccountService.updateLoyalty(userId, request));
    }

    @GetMapping("/refunds")
    public Result<List<ServiceOrderResponse>> listRefunds() {
        return Result.success(serviceOrderService.listRefundRequestsForAdmin());
    }

    @PostMapping("/refunds/{orderId}")
    public Result<ServiceOrderResponse> handleRefund(@PathVariable Long orderId,
                                                     @Valid @RequestBody RefundDecisionRequest request) {
        return Result.success("操作成功", serviceOrderService.handleRefund(orderId, request));
    }
}
