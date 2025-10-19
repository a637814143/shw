package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 家政公司人员信息请求。
 */
public class CompanyStaffRequest {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名过长")
    private String staffName;

    @NotBlank(message = "联系方式不能为空")
    @Size(max = 100, message = "联系方式过长")
    private String staffPhone;

    @Size(max = 500, message = "备注信息过长")
    private String remarks;

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffPhone() {
        return staffPhone;
    }

    public void setStaffPhone(String staffPhone) {
        this.staffPhone = staffPhone;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
