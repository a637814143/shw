package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.CertificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CertificationReviewRequest(
        @NotNull CertificationStatus status,
        @NotBlank String reviewedBy,
        String remark
) {}
