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

    public ServiceReviewResponse(Long id, Long serviceId, String serviceName, String username, Integer rating,
                                 String content, Instant createdAt) {
        this.id = id;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.username = username;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
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
}
