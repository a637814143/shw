package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotNull;

public class ServiceReviewRequest {

    @NotNull(message = "请选择是否通过")
    private Boolean approve;

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
