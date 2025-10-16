package com.example.housekeeping.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 管理员调整余额请求。
 */
public class UpdateWalletRequest {

    @NotNull(message = "请输入金额")
    @DecimalMin(value = "0.00", message = "金额不能小于0")
    private BigDecimal money;

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}
