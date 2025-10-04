package com.example.housekeeping.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 预约请求体
 */
public class BookingRequest {

    @NotBlank(message = "请填写预约人姓名")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String customerName;

    @NotBlank(message = "请填写联系电话")
    @Size(max = 20, message = "联系电话长度不能超过20个字符")
    private String phone;

    @NotBlank(message = "请填写服务地址")
    private String address;

    @NotNull(message = "请选择预约日期")
    @FutureOrPresent(message = "预约日期不能早于今天")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate serviceDate;

    private String notes;

    @NotNull(message = "请选择服务项目")
    private Long serviceItemId;

    /**
     * 后台管理员代客创建时可指定创建人
     */
    private String createdBy;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getServiceItemId() {
        return serviceItemId;
    }

    public void setServiceItemId(Long serviceItemId) {
        this.serviceItemId = serviceItemId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
