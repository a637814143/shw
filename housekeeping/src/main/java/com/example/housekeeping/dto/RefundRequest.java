package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户发起退款请求。
 */
public class RefundRequest {

    @NotBlank(message = "请填写退款原因")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
