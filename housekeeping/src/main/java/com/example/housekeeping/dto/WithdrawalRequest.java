package com.example.housekeeping.dto;

import com.example.housekeeping.entity.WithdrawalAccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WithdrawalRequest {

    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "提现金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "提现账户类型不能为空")
    private WithdrawalAccountType accountType;

    @NotBlank(message = "账号不能为空")
    private String accountNumber;

    @NotBlank(message = "服务者名称不能为空")
    private String providerName;

    @NotNull(message = "提现时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime withdrawalTime;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public WithdrawalAccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(WithdrawalAccountType accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public LocalDateTime getWithdrawalTime() {
        return withdrawalTime;
    }

    public void setWithdrawalTime(LocalDateTime withdrawalTime) {
        this.withdrawalTime = withdrawalTime;
    }
}
