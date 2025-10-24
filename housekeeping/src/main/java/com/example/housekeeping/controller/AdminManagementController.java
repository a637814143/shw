package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.AccountTransactionResponse;
import com.example.housekeeping.dto.AdminOverviewResponse;
import com.example.housekeeping.dto.AssignWorkerRequest;
import com.example.housekeeping.dto.RefundDecisionRequest;
import com.example.housekeeping.dto.ServiceFavoriteResponse;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.dto.UpdateLoyaltyRequest;
import com.example.housekeeping.dto.UpdatePasswordRequest;
import com.example.housekeeping.dto.UpdateWalletRequest;
import com.example.housekeeping.dto.UserAccountResponse;
import com.example.housekeeping.dto.IdListRequest;
import com.example.housekeeping.service.AdminAccountService;
import com.example.housekeeping.service.AdminInsightService;
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

    @Autowired
    private AdminInsightService adminInsightService;

    @GetMapping("/overview")
    public Result<AdminOverviewResponse> overview() {
        return Result.success(adminInsightService.loadOverview());
    }

    @GetMapping("/users")
    public Result<List<UserAccountResponse>> listUsers(
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return Result.success(adminAccountService.listUsers(keyword));
    }

    @GetMapping("/companies")
    public Result<List<UserAccountResponse>> listCompanies(
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return Result.success(adminAccountService.listCompanies(keyword));
    }

    @GetMapping("/managers")
    public Result<List<UserAccountResponse>> listManagers(
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return Result.success(adminAccountService.listAdmins(keyword));
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

    @DeleteMapping("/users/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        adminAccountService.deleteAccount(userId);
        return Result.success("账号已删除", null);
    }

    @DeleteMapping("/users/batch")
    public Result<Void> deleteUsers(@Valid @RequestBody IdListRequest request) {
        adminAccountService.deleteAccounts(request.getIds());
        return Result.success("账号已删除", null);
    }

    @GetMapping("/refunds")
    public Result<List<ServiceOrderResponse>> listRefunds(
        @RequestParam(value = "stage", defaultValue = "pending") String stage,
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return Result.success(serviceOrderService.listRefundsForAdmin(stage, keyword));
    }

    @PostMapping("/refunds/{orderId}")
    public Result<ServiceOrderResponse> handleRefund(@PathVariable Long orderId,
                                                     @Valid @RequestBody RefundDecisionRequest request) {
        return Result.success("操作成功", serviceOrderService.handleRefund(orderId, request));
    }

    @DeleteMapping("/refunds")
    public Result<Void> deleteRefunds(@Valid @RequestBody IdListRequest request) {
        serviceOrderService.deleteProcessedRefunds(request.getIds());
        return Result.success("删除成功", null);
    }

    @GetMapping("/orders")
    public Result<List<ServiceOrderResponse>> listOrders(
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return Result.success(serviceOrderService.listOrdersForAdmin(keyword));
    }

    @PostMapping("/orders/{orderId}/settle")
    public Result<ServiceOrderResponse> settleOrder(@PathVariable Long orderId) {
        return Result.success("结算成功", serviceOrderService.settleCompletedOrder(orderId));
    }

    @PostMapping("/orders/{orderId}/assign")
    public Result<ServiceOrderResponse> assignWorker(@PathVariable Long orderId,
                                                     @Valid @RequestBody AssignWorkerRequest request) {
        return Result.success("分配成功", serviceOrderService.assignWorker(orderId, request));
    }

    @GetMapping("/transactions")
    public Result<List<AccountTransactionResponse>> listTransactions() {
        return Result.success(adminInsightService.listRecentTransactions());
    }

    @GetMapping("/favorites")
    public Result<List<ServiceFavoriteResponse>> listFavorites() {
        return Result.success(adminInsightService.listFavorites());
    }
}
