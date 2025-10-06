package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.ProviderCertification;
import com.example.housekeeping.domain.enums.CertificationStatus;
import com.example.housekeeping.dto.CertificationReviewRequest;
import com.example.housekeeping.dto.CertificationSubmissionRequest;
import com.example.housekeeping.service.CertificationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certifications")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    @PostMapping("/provider/{providerId}")
    public ProviderCertification submitCertification(@PathVariable Long providerId,
            @Valid @RequestBody CertificationSubmissionRequest request) {
        return certificationService.submitCertification(providerId, request);
    }

    @PatchMapping("/{certificationId}")
    public ProviderCertification reviewCertification(@PathVariable Long certificationId,
            @Valid @RequestBody CertificationReviewRequest request) {
        return certificationService.reviewCertification(certificationId, request);
    }

    @GetMapping
    public List<ProviderCertification> listCertifications(@RequestParam(required = false) CertificationStatus status) {
        return certificationService.listCertifications(status);
    }

    @GetMapping("/provider/{providerId}/latest")
    public ProviderCertification latestCertification(@PathVariable Long providerId) {
        return certificationService.latestCertification(providerId);
    }
}
