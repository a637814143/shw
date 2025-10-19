package com.example.housekeeping.dto;

import java.math.BigDecimal;

/**
 * 当前登录账号的基础信息。
 */
public class AccountProfileResponse {

    private Long id;
    private String username;
    private String displayName;
    private String role;
    private BigDecimal balance;
    private Integer loyaltyPoints;
    private String avatarBase64;

    public AccountProfileResponse(
        Long id,
        String username,
        String displayName,
        String role,
        BigDecimal balance,
        Integer loyaltyPoints,
        String avatarBase64
    ) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.balance = balance;
        this.loyaltyPoints = loyaltyPoints;
        this.avatarBase64 = avatarBase64;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
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

    public String getAvatarBase64() {
        return avatarBase64;
    }
}
