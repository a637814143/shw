package com.example.housekeeping.dto;

import java.math.BigDecimal;

/**
 * 当前登录账号的基础信息。
 */
public class AccountProfileResponse {

    private Long id;
    private String username;
    private String role;
    private BigDecimal balance;

    public AccountProfileResponse(Long id, String username, String role, BigDecimal balance) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
