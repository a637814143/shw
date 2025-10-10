package com.example.housekeeping.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeCreateRequest {

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal amount;

    private String paymentMethod;
}
