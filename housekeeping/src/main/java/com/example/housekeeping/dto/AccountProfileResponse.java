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
    private Integer loyaltyPoints;

    public AccountProfileResponse(Long id, String username, String role, BigDecimal balance, Integer loyaltyPoints) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.balance = balance;
        this.loyaltyPoints = loyaltyPoints;
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

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }
}
