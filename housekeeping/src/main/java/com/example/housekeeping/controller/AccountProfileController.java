package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.AccountProfileResponse;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.service.AccountLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用账号信息接口，供所有已登录角色查询。
 */
@RestController
@RequestMapping("/api/account")
public class AccountProfileController {

    @Autowired
    private AccountLookupService accountLookupService;

    @GetMapping("/me")
    public Result<AccountProfileResponse> currentAccount() {
        UserAll current = accountLookupService.getCurrentAccount();
        AccountRole role = AccountRole.fromValue(current.getUserType());
        AccountProfileResponse response = new AccountProfileResponse(
            current.getId(),
            current.getUsername(),
            role.getCode(),
            current.getMoney()
        );
        return Result.success(response);
    }
}
