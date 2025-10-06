package com.example.housekeeping.service.impl;

import com.example.housekeeping.domain.entity.ProviderCertification;
import com.example.housekeeping.domain.entity.ServiceProvider;
import com.example.housekeeping.domain.enums.CertificationStatus;
import com.example.housekeeping.dto.CertificationReviewRequest;
import com.example.housekeeping.dto.CertificationSubmissionRequest;
import com.example.housekeeping.repository.ProviderCertificationRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.service.CertificationService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificationServiceImpl implements CertificationService {

    private final ProviderCertificationRepository providerCertificationRepository;
    private final ServiceProviderRepository serviceProviderRepository;

    @Override
    public ProviderCertification submitCertification(Long providerId, CertificationSubmissionRequest request) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "服务者不存在"));
        ProviderCertification certification = new ProviderCertification();
        certification.setProvider(provider);
        certification.setDocuments(request.documents());
        certification.setExperienceYears(request.experienceYears());
        certification.setSpeciality(request.speciality());
        certification.setStatus(CertificationStatus.PENDING);
        certification.setSubmittedAt(LocalDateTime.now());
        provider.setCertified(false);
        return providerCertificationRepository.save(certification);
    }

    @Override
    public ProviderCertification reviewCertification(Long certificationId, CertificationReviewRequest request) {
        ProviderCertification certification = providerCertificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "认证记录不存在"));
        certification.setStatus(request.status());
        certification.setAuditRemark(request.remark());
        certification.setReviewedBy(request.reviewedBy());
        certification.setReviewedAt(LocalDateTime.now());
        if (request.status() == CertificationStatus.APPROVED) {
            ServiceProvider provider = certification.getProvider();
            provider.setCertified(true);
        }
        return certification;
    }

    @Override
    public List<ProviderCertification> listCertifications(CertificationStatus status) {
        if (status == null) {
            return providerCertificationRepository.findAll();
        }
        return providerCertificationRepository.findByStatus(status);
    }

    @Override
    public ProviderCertification latestCertification(Long providerId) {
        return providerCertificationRepository.findTopByProviderIdOrderByCreatedAtDesc(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "暂无认证记录"));
    }
}
