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

    public UserAccountResponse(Long id, String username, String role, BigDecimal balance) {
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
