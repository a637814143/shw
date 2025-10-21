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
    private String contactPhone;
    private String contactAddress;
    private String companyDescription;

    public AccountProfileResponse(
        Long id,
        String username,
        String displayName,
        String role,
        BigDecimal balance,
        Integer loyaltyPoints,
        String avatarBase64,
        String contactPhone,
        String contactAddress,
        String companyDescription
    ) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.balance = balance;
        this.loyaltyPoints = loyaltyPoints;
        this.avatarBase64 = avatarBase64;
        this.contactPhone = contactPhone;
        this.contactAddress = contactAddress;
        this.companyDescription = companyDescription;
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

    public String getContactPhone() {
        return contactPhone;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }
}
