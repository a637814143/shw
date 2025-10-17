package com.example.housekeeping.dto;

import com.example.housekeeping.enums.ServiceOrderStatus;

import java.time.Instant;

/**
 * 普通用户的会话摘要。
 */
public class UserConversationSummaryResponse {

    private final Long orderId;
    private final String serviceName;
    private final String companyName;
    private final ServiceOrderStatus status;
    private final String lastMessage;
    private final Instant lastMessageAt;
    private final long unreadCount;

    public UserConversationSummaryResponse(Long orderId, String serviceName, String companyName,
                                           ServiceOrderStatus status, String lastMessage,
                                           Instant lastMessageAt, long unreadCount) {
        this.orderId = orderId;
        this.serviceName = serviceName;
        this.companyName = companyName;
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

    public String getCompanyName() {
        return companyName;
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
