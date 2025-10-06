package com.example.housekeeping.dto;

import java.math.BigDecimal;

public record RechargeRequest(
        BigDecimal amount,
        String paymentMethod
) {}
