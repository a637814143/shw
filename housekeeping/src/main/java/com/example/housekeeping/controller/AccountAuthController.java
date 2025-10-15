package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.AccountLoginRequest;
import com.example.housekeeping.dto.AccountRegisterRequest;
import com.example.housekeeping.dto.AccountSummary;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.service.AccountAuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 面向前台登录注册的账号控制器
 */
@RestController
@RequestMapping("/api/public/accounts")
public class AccountAuthController {

    @Autowired
    private AccountAuthService accountAuthService;

    @PostMapping("/register")
    public Result<AccountSummary> register(@Valid @RequestBody AccountRegisterRequest request) {
        try {
            AccountSummary summary = accountAuthService.register(request);
            return Result.success("注册成功", summary);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody AccountLoginRequest request) {
        try {
            LoginResponse response = accountAuthService.login(request);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
