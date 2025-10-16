package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 公司或管理员处理退款的请求。
 */
public class RefundDecisionRequest {

    @NotNull(message = "请选择处理结果")
    private Boolean approve;

    @Size(max = 500, message = "回复过长")
    private String message;

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
