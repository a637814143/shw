package com.example.housekeeping.dto;

import java.time.Instant;

/**
 * 企业消息返回体。
 */
public class CompanyMessageResponse {

    private final Long id;
    private final Long orderId;
    private final String serviceName;
    private final String senderName;
    private final String recipientName;
    private final String content;
    private final Instant createdAt;
    private final Instant readAt;
    private final boolean incoming;

    public CompanyMessageResponse(Long id, Long orderId, String serviceName, String senderName, String recipientName,
                                  String content, Instant createdAt, Instant readAt, boolean incoming) {
        this.id = id;
        this.orderId = orderId;
        this.serviceName = serviceName;
        this.senderName = senderName;
        this.recipientName = recipientName;
        this.content = content;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.incoming = incoming;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public boolean isIncoming() {
        return incoming;
    }
}
