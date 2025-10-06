package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.WithdrawalStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WithdrawalProcessRequest(
        @NotNull WithdrawalStatus status,
        @NotBlank String processedBy,
        String remark
) {}
