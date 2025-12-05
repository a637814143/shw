package com.example.housekeeping.dto;

import java.math.BigDecimal;

/**
 * 家政服务返回信息。
 */
public class HousekeepServiceResponse {

    private Long id;
    private String name;
    private String unit;
    private BigDecimal price;
    private String contact;
    private String serviceTime;
    private String description;
    private Long companyId;
    private String companyName;
    private Long categoryId;
    private String categoryName;
    private Long availableStaffCount;
    private String status;
    private String rejectionReason;

    public HousekeepServiceResponse(Long id, String name, String unit, BigDecimal price, String contact,
                                    String serviceTime, String description, Long companyId, String companyName,
                                    Long categoryId, String categoryName, Long availableStaffCount,
                                    String status, String rejectionReason) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.contact = contact;
        this.serviceTime = serviceTime;
        this.description = description;
        this.companyId = companyId;
        this.companyName = companyName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.availableStaffCount = availableStaffCount;
        this.status = status;
        this.rejectionReason = rejectionReason;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getContact() {
        return contact;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public String getDescription() {
        return description;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Long getAvailableStaffCount() {
        return availableStaffCount;
    }

    public String getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }
}
