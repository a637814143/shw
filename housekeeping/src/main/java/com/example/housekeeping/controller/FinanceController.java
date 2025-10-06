package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.RechargeRecord;
import com.example.housekeeping.domain.entity.WithdrawalRecord;
import com.example.housekeeping.dto.RechargeRequest;
import com.example.housekeeping.dto.WithdrawalProcessRequest;
import com.example.housekeeping.dto.WithdrawalRequest;
import com.example.housekeeping.service.FinanceService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finance")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @PostMapping("/recharge/user/{userId}")
    public RechargeRecord createRecharge(@PathVariable Long userId, @RequestBody RechargeRequest request) {
        return financeService.createRecharge(userId, request);
    }

    @GetMapping("/recharge/user/{userId}")
    public List<RechargeRecord> getUserRecharges(@PathVariable Long userId) {
        return financeService.getRechargesForUser(userId);
    }

    @GetMapping("/recharge")
    public List<RechargeRecord> getAllRecharges() {
        return financeService.getAllRecharges();
    }

    @PostMapping("/withdraw/provider/{providerId}")
    public WithdrawalRecord requestWithdrawal(@PathVariable Long providerId, @RequestBody WithdrawalRequest request) {
        return financeService.requestWithdrawal(providerId, request);
    }

    @PatchMapping("/withdraw/{withdrawalId}")
    public WithdrawalRecord processWithdrawal(@PathVariable Long withdrawalId,
            @RequestBody WithdrawalProcessRequest request) {
        return financeService.processWithdrawal(withdrawalId, request);
    }

    @GetMapping("/withdraw/provider/{providerId}")
    public List<WithdrawalRecord> getProviderWithdrawals(@PathVariable Long providerId) {
        return financeService.getWithdrawalsForProvider(providerId);
    }

    @GetMapping("/withdraw")
    public List<WithdrawalRecord> getAllWithdrawals() {
        return financeService.getAllWithdrawals();
    }
}
