package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 管理员为订单指派家政人员。
 */
public class AssignWorkerRequest {

    @NotBlank(message = "家政人员姓名不能为空")
    @Size(max = 100, message = "姓名过长")
    private String workerName;

    @NotBlank(message = "联系方式不能为空")
    @Size(max = 100, message = "联系方式过长")
    private String workerContact;

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
}
