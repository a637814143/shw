package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 编辑个人资料请求。
 */
public class AccountProfileUpdateRequest {

    @NotBlank(message = "展示名称不能为空")
    @Size(max = 100, message = "展示名称长度不得超过100个字符")
    private String displayName;

    @Size(max = 524288, message = "头像数据过大，请控制在512KB以内")
    private String avatarBase64;

    @Size(max = 100, message = "联系方式长度不得超过100个字符")
    private String contactNumber;

    @Size(max = 255, message = "地址长度不得超过255个字符")
    private String address;

    @Size(max = 100, message = "联系电话长度不得超过100个字符")
    private String companyPhone;

    @Size(max = 255, message = "公司地址长度不得超过255个字符")
    private String companyAddress;

    @Size(max = 1000, message = "公司简介长度不得超过1000个字符")
    private String companyDescription;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarBase64() {
        return avatarBase64;
    }

    public void setAvatarBase64(String avatarBase64) {
        this.avatarBase64 = avatarBase64;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }
}
