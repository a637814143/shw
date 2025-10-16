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
    private String refundReason;
    private String refundResponse;
    private String handledBy;
    private Instant createdAt;
    private Instant updatedAt;

    public ServiceOrderResponse(Long id, Long serviceId, String serviceName, String unit, BigDecimal price,
                                String contact, String companyName, String username, ServiceOrderStatus status,
                                String refundReason, String refundResponse, String handledBy,
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
        this.refundReason = refundReason;
        this.refundResponse = refundResponse;
        this.handledBy = handledBy;
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

    public String getRefundReason() {
        return refundReason;
    }

    public String getRefundResponse() {
        return refundResponse;
    }

    public String getHandledBy() {
        return handledBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
