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
    private String contactNumber;
    private String address;
    private String companyPhone;
    private String companyAddress;
    private String companyDescription;

    public AccountProfileResponse(
        Long id,
        String username,
        String displayName,
        String role,
        BigDecimal balance,
        Integer loyaltyPoints,
        String contactNumber,
        String address,
        String companyPhone,
        String companyAddress,
        String companyDescription
    ) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.balance = balance;
        this.loyaltyPoints = loyaltyPoints;
        this.contactNumber = contactNumber;
        this.address = address;
        this.companyPhone = companyPhone;
        this.companyAddress = companyAddress;
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

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }
}
