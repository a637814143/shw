package com.example.housekeeping.dto;

import com.example.housekeeping.enums.ServiceOrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 服务订单展示信息。
 */
public class ServiceOrderResponse {

    private Long id;
    private Long serviceId;
    private String serviceName;
    private String unit;
    private BigDecimal price;
    private String contact;
    private String companyName;
    private String username;
    private ServiceOrderStatus status;
    private Instant scheduledAt;
    private String specialRequest;
    private String serviceAddress;
    private String progressNote;
    private Integer loyaltyPoints;
    private String refundReason;
    private String refundResponse;
    private String handledBy;
    private String assignedWorker;
    private String workerContact;
    private String customerContactPhone;
    private String customerAddress;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean settlementReleased;
    private Instant settlementReleasedAt;
    private Long categoryId;
    private String categoryName;
    private Long assignedStaffId;

    public ServiceOrderResponse(Long id, Long serviceId, String serviceName, String unit, BigDecimal price,
                                String contact, String companyName, String username, ServiceOrderStatus status,
                                Instant scheduledAt, String specialRequest, String serviceAddress, String progressNote, Integer loyaltyPoints,
                                String refundReason, String refundResponse, String handledBy,
                                String assignedWorker, String workerContact,
                                String customerContactPhone, String customerAddress,
                                Instant createdAt, Instant updatedAt, boolean settlementReleased, Instant settlementReleasedAt,
                                Long categoryId, String categoryName, Long assignedStaffId) {
        this.id = id;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.unit = unit;
        this.price = price;
        this.contact = contact;
        this.companyName = companyName;
        this.username = username;
        this.status = status;
        this.scheduledAt = scheduledAt;
        this.specialRequest = specialRequest;
        this.serviceAddress = serviceAddress;
        this.progressNote = progressNote;
        this.loyaltyPoints = loyaltyPoints;
        this.refundReason = refundReason;
        this.refundResponse = refundResponse;
        this.handledBy = handledBy;
        this.assignedWorker = assignedWorker;
        this.workerContact = workerContact;
        this.customerContactPhone = customerContactPhone;
        this.customerAddress = customerAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.settlementReleased = settlementReleased;
        this.settlementReleasedAt = settlementReleasedAt;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.assignedStaffId = assignedStaffId;
    }

    public Long getId() {
        return id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
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

    public String getCompanyName() {
        return companyName;
    }

    public String getUsername() {
        return username;
    }

    public ServiceOrderStatus getStatus() {
        return status;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public String getProgressNote() {
        return progressNote;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public String getRefundResponse() {
        return refundResponse;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public String getAssignedWorker() {
        return assignedWorker;
    }

    public String getWorkerContact() {
        return workerContact;
    }

    public String getCustomerContactPhone() {
        return customerContactPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isSettlementReleased() {
        return settlementReleased;
    }

    public Instant getSettlementReleasedAt() {
        return settlementReleasedAt;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Long getAssignedStaffId() {
        return assignedStaffId;
    }
}
