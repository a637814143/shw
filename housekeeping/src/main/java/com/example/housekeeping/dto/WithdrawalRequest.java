package com.example.housekeeping.dto;

import java.math.BigDecimal;

public record WithdrawalRequest(
        BigDecimal amount,
        String accountInfo
) {}
