package com.example.housekeeping.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 创建或更新家政服务的请求。
 */
public class HousekeepServiceRequest {

    @NotBlank(message = "服务名称不能为空")
    @Size(max = 200, message = "服务名称过长")
    private String name;

    @NotBlank(message = "服务单位不能为空")
    @Size(max = 50, message = "服务单位过长")
    private String unit;

    @DecimalMin(value = "0.00", inclusive = false, message = "价格需大于0")
    private BigDecimal price;

    @NotBlank(message = "联系方式不能为空")
    @Size(max = 100, message = "联系方式过长")
    private String contact;

    @NotBlank(message = "服务时间不能为空")
    @Size(max = 100, message = "服务时间过长")
    private String serviceTime;

    @Size(max = 500, message = "描述过长")
    private String description;

    @NotNull(message = "请选择服务分类")
    private Long categoryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
