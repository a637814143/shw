package com.example.housekeeping.dto;

import com.example.housekeeping.enums.ServiceOrderStatus;

import java.time.Instant;

/**
 * 企业侧客户会话概览。
 */
public class CompanyConversationSummaryResponse {

    private final Long orderId;
    private final String serviceName;
    private final String customerName;
    private final ServiceOrderStatus status;
    private final String lastMessage;
    private final Instant lastMessageAt;
    private final long unreadCount;

    public CompanyConversationSummaryResponse(Long orderId, String serviceName, String customerName,
                                              ServiceOrderStatus status, String lastMessage, Instant lastMessageAt,
                                              long unreadCount) {
        this.orderId = orderId;
        this.serviceName = serviceName;
        this.customerName = customerName;
        this.status = status;
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
        this.unreadCount = unreadCount;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public ServiceOrderStatus getStatus() {
        return status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }

    public long getUnreadCount() {
        return unreadCount;
    }
}
