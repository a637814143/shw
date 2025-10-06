package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.WithdrawalStatus;

public record WithdrawalProcessRequest(
        WithdrawalStatus status,
        String processedBy,
        String remark
) {}
