package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;

public class AssignAppointmentRequest {

    @NotBlank(message = "服务者名称不能为空")
    private String providerName;

    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    private String contactAddress;

    private String servicePhone;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactAddress() {
        return contactAddress;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }
}
