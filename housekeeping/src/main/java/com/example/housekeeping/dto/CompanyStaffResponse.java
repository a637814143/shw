package com.example.housekeeping.dto;

import java.time.Instant;

/**
 * 家政公司人员信息响应。
 */
public class CompanyStaffResponse {

    private final Long id;
    private final String name;
    private final String contact;
    private final String role;
    private final String notes;
    private final String status;
    private final String statusLabel;
    private final Instant createdAt;
    private final Instant updatedAt;

    public CompanyStaffResponse(Long id, String name, String contact, String role, String notes,
                                String status, String statusLabel,
                                Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.role = role;
        this.notes = notes;
        this.status = status;
        this.statusLabel = statusLabel;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getRole() {
        return role;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
