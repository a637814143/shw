package com.example.housekeeping.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

/**
 * 为订单指派家政人员请求。
 */
public class AssignOrderStaffRequest {

    private Long staffId;

    @Size(max = 100, message = "姓名过长")
    private String workerName;

    @Size(max = 100, message = "联系方式过长")
    private String workerContact;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerContact() {
        return workerContact;
    }

    public void setWorkerContact(String workerContact) {
        this.workerContact = workerContact;
    }

    @AssertTrue(message = "请指定家政人员或填写姓名与联系方式")
    public boolean isValidSelection() {
        boolean hasStaff = staffId != null;
        boolean hasManual = workerName != null && !workerName.trim().isEmpty()
            && workerContact != null && !workerContact.trim().isEmpty();
        return hasStaff || hasManual;
    }
}
