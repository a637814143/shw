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
    private String progressNote;
    private String serviceAddress;
    private String contactPhone;
    private Integer loyaltyPoints;
    private String refundReason;
    private String refundResponse;
    private String handledBy;
    private String assignedWorker;
    private String workerContact;
    private Long assignedStaffId;
    private Instant createdAt;
    private Instant updatedAt;

    public ServiceOrderResponse(Long id, Long serviceId, String serviceName, String unit, BigDecimal price,
                                String contact, String companyName, String username, ServiceOrderStatus status,
                                Instant scheduledAt, String specialRequest, String progressNote, String serviceAddress,
                                String contactPhone, Integer loyaltyPoints,
                                String refundReason, String refundResponse, String handledBy,
                                String assignedWorker, String workerContact, Long assignedStaffId,
                                Instant createdAt, Instant updatedAt) {
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
        this.progressNote = progressNote;
        this.serviceAddress = serviceAddress;
        this.contactPhone = contactPhone;
        this.loyaltyPoints = loyaltyPoints;
        this.refundReason = refundReason;
        this.refundResponse = refundResponse;
        this.handledBy = handledBy;
        this.assignedWorker = assignedWorker;
        this.workerContact = workerContact;
        this.assignedStaffId = assignedStaffId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getProgressNote() {
        return progressNote;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public String getContactPhone() {
        return contactPhone;
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

    public Long getAssignedStaffId() {
        return assignedStaffId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
