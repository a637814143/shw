package com.example.housekeeping.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * 用户选择服务的请求。
 */
public class ServiceOrderRequest {

    @NotNull(message = "请选择服务")
    private Long serviceId;

    @NotNull(message = "请选择预约时间")
    @FutureOrPresent(message = "预约时间不能早于当前")
    private Instant scheduledAt;

    @Size(max = 500, message = "特殊需求过长")
    private String specialRequest;

    @NotBlank(message = "请填写上门地址")
    @Size(max = 255, message = "上门地址过长")
    private String serviceAddress;

    @Size(max = 100, message = "联系方式过长")
    private String contactPhone;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(Instant scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public void setSpecialRequest(String specialRequest) {
        this.specialRequest = specialRequest;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
