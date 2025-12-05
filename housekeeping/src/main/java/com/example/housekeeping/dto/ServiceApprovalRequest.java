package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 管理员审核服务请求。
 */
public class ServiceApprovalRequest {

    @NotNull(message = "请选择审核结果")
    private Boolean approve;

    @Size(max = 500, message = "驳回理由不能超过500字")
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
