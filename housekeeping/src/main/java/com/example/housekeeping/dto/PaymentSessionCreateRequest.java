package com.example.housekeeping.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 创建扫码支付会话的请求参数。
 */
public class PaymentSessionCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String serviceName;

    @Size(max = 100)
    private String companyName;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal amount;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
