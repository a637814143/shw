package com.example.housekeeping.dto;

import java.time.Instant;

/**
 * 家政公司人员信息响应。
 */
public class CompanyStaffResponse {

    private final Long id;
    private final String staffName;
    private final String staffPhone;
    private final String remarks;
    private final Instant createdAt;
    private final Instant updatedAt;

    public CompanyStaffResponse(Long id, String staffName, String staffPhone, String remarks,
                                Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.staffName = staffName;
        this.staffPhone = staffPhone;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getStaffName() {
        return staffName;
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public String getRemarks() {
        return remarks;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
