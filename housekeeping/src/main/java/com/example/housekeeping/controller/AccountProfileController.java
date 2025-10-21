package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.AccountPasswordUpdateRequest;
import com.example.housekeeping.dto.AccountProfileResponse;
import com.example.housekeeping.dto.AccountProfileUpdateRequest;
import com.example.housekeeping.service.AccountProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用账号信息接口，供所有已登录角色查询。
 */
@RestController
@RequestMapping("/api/account")
public class AccountProfileController {

    @Autowired
    private AccountProfileService accountProfileService;

    @GetMapping("/me")
    public Result<AccountProfileResponse> currentAccount() {
        AccountProfileResponse response = accountProfileService.currentProfile();
        return Result.success(response);
    }

    @PutMapping("/me")
    public Result<AccountProfileResponse> updateAccount(@Valid @RequestBody AccountProfileUpdateRequest request) {
        AccountProfileResponse response = accountProfileService.updateProfile(request);
        return Result.success("资料更新成功", response);
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody AccountPasswordUpdateRequest request) {
        accountProfileService.updatePassword(request);
        return Result.success("密码修改成功", null);
    }
}
