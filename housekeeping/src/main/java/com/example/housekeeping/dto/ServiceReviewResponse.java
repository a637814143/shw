package com.example.housekeeping.dto;

import java.time.Instant;

/**
 * 服务评价返回信息。
 */
public class ServiceReviewResponse {

    private Long id;
    private Long serviceId;
    private String serviceName;
    private String username;
    private Integer rating;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private String companyReply;
    private Instant replyAt;
    private Boolean pinned;

    public ServiceReviewResponse(Long id, Long serviceId, String serviceName, String username, Integer rating,
                                 String content, Instant createdAt, Instant updatedAt, String companyReply,
                                 Instant replyAt, Boolean pinned) {
        this.id = id;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.username = username;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.companyReply = companyReply;
        this.replyAt = replyAt;
        this.pinned = pinned;
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

    public String getUsername() {
        return username;
    }

    public Integer getRating() {
        return rating;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCompanyReply() {
        return companyReply;
    }

    public Instant getReplyAt() {
        return replyAt;
    }

    public Boolean getPinned() {
        return pinned;
    }
}
