package com.example.housekeeping.service;

import com.example.housekeeping.domain.entity.ProviderCertification;
import com.example.housekeeping.domain.enums.CertificationStatus;
import com.example.housekeeping.dto.CertificationReviewRequest;
import com.example.housekeeping.dto.CertificationSubmissionRequest;
import java.util.List;

public interface CertificationService {
    ProviderCertification submitCertification(Long providerId, CertificationSubmissionRequest request);

    ProviderCertification reviewCertification(Long certificationId, CertificationReviewRequest request);

    List<ProviderCertification> listCertifications(CertificationStatus status);

    ProviderCertification latestCertification(Long providerId);
}
