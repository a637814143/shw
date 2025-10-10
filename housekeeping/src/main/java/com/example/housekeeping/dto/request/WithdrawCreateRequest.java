package com.example.housekeeping.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawCreateRequest {

    @NotNull
    private Long providerId;

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String bankName;

    @NotBlank
    private String bankAccount;

    @NotBlank
    private String accountHolder;
}
