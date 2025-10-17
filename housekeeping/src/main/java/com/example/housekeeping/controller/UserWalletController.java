package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.AccountProfileResponse;
import com.example.housekeeping.dto.PointsExchangeRequest;
import com.example.housekeeping.dto.WalletRechargeRequest;
import com.example.housekeeping.service.UserWalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户钱包相关接口。
 */
@RestController
@RequestMapping("/api/user/wallet")
public class UserWalletController {

    @Autowired
    private UserWalletService userWalletService;

    @PostMapping("/recharge")
    public Result<AccountProfileResponse> recharge(@Valid @RequestBody WalletRechargeRequest request) {
        return Result.success("充值成功", userWalletService.recharge(request));
    }

    @PostMapping("/exchange")
    public Result<AccountProfileResponse> exchange(@Valid @RequestBody PointsExchangeRequest request) {
        return Result.success("兑换成功", userWalletService.exchangePoints(request));
    }
}
