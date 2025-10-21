package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 家政公司人员信息的创建/修改请求。
 */
public class CompanyStaffRequest {

    @NotBlank(message = "请填写人员姓名")
    @Size(max = 100, message = "姓名长度不得超过100个字符")
    private String name;

    @NotBlank(message = "请填写联系方式")
    @Size(max = 100, message = "联系方式长度不得超过100个字符")
    private String contact;

    @Size(max = 100, message = "职位长度不得超过100个字符")
    private String role;

    @Size(max = 500, message = "备注长度不得超过500个字符")
    private String notes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
