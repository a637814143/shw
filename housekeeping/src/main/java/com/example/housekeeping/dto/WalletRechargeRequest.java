package com.example.housekeeping.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * 用户充值请求。
 */
public class WalletRechargeRequest {

    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额需大于0")
    @Digits(integer = 10, fraction = 2, message = "金额格式不正确")
    private BigDecimal amount;

    @NotBlank(message = "二维码认证信息不能为空")
    private String qrToken;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }
}
