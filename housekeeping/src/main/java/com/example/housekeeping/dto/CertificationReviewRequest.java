package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.CertificationStatus;

public record CertificationReviewRequest(
        CertificationStatus status,
        String reviewedBy,
        String remark
) {}
