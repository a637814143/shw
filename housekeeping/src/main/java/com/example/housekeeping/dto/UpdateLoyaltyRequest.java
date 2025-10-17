package com.example.housekeeping.dto;

import jakarta.validation.constraints.Min;

/**
 * 管理员调整积分的请求。
 */
public class UpdateLoyaltyRequest {

    @Min(value = 0, message = "积分不能为负数")
    private Integer loyaltyPoints;

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}
