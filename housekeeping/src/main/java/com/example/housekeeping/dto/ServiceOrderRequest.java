package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 用户选择服务的请求。
 */
public class ServiceOrderRequest {

    @NotNull(message = "请选择服务")
    private Long serviceId;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
