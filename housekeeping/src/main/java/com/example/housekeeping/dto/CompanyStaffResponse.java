package com.example.housekeeping.dto;

import java.time.Instant;
import java.util.List;

/**
 * 家政公司人员信息响应。
 */
public class CompanyStaffResponse {

    private final Long id;
    private final String name;
    private final String contact;
    private final String notes;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Long categoryId;
    private final String categoryName;
    private final boolean assigned;
    private final List<String> serviceTimeSlots;

    public CompanyStaffResponse(Long id, String name, String contact, String notes,
                                Instant createdAt, Instant updatedAt,
                                Long categoryId, String categoryName, boolean assigned,
                                List<String> serviceTimeSlots) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.assigned = assigned;
        this.serviceTimeSlots = serviceTimeSlots;
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

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public List<String> getServiceTimeSlots() {
        return serviceTimeSlots;
    }
}
