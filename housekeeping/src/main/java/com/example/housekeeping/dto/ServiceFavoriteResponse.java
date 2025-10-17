package com.example.housekeeping.dto;

import java.time.Instant;

/**
 * 收藏信息响应。
 */
public class ServiceFavoriteResponse {

    private final Long id;
    private final String username;
    private final Long serviceId;
    private final String serviceName;
    private final String companyName;
    private final Instant createdAt;

    public ServiceFavoriteResponse(Long id, String username, Long serviceId, String serviceName, String companyName,
                                   Instant createdAt) {
        this.id = id;
        this.username = username;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.companyName = companyName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
