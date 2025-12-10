package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ServiceApprovalRequest {

    @NotNull(message = "请选择是否通过")
    private Boolean approve;

    @Size(max = 500, message = "驳回理由长度需在500字内")
    private String reason;

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
