package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 指派公司人员到订单的请求。
 */
public class AssignStaffRequest {

    @NotNull(message = "请选择需要指派的订单")
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
