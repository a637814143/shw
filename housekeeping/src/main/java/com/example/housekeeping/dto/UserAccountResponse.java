package com.example.housekeeping.dto;

import java.math.BigDecimal;

/**
 * 管理员查看的用户信息。
 */
public class UserAccountResponse {

    private Long id;
    private String username;
    private String role;
    private BigDecimal balance;
    private Integer loyaltyPoints;

    public UserAccountResponse(Long id, String username, String role, BigDecimal balance, Integer loyaltyPoints) {
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
