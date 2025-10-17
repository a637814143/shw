package com.example.housekeeping.dto;

import jakarta.validation.constraints.Min;

/**
 * 积分兑换请求。
 */
public class PointsExchangeRequest {

    @Min(value = 5, message = "兑换积分需不小于5")
    private int points;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
