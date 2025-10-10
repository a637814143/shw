package com.example.housekeeping.controller.provider;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.ProviderCertificationRequest;
import com.example.housekeeping.entity.ProviderCertification;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.enums.CertificationStatus;
import com.example.housekeeping.repository.ProviderCertificationRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider/certification")
@RequiredArgsConstructor
public class ProviderCertificationController {

    private final ProviderCertificationRepository certificationRepository;
    private final ServiceProviderRepository providerRepository;

    @GetMapping
    public ApiResponse<ProviderCertification> detail(@RequestParam Long providerId) {
        ServiceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        return ApiResponse.success(certificationRepository.findByProvider(provider).orElse(null));
    }

    @PostMapping
    public ApiResponse<ProviderCertification> submit(@RequestBody @Valid ProviderCertificationRequest request) {
        ServiceProvider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        ProviderCertification certification = certificationRepository.findByProvider(provider)
                .orElseGet(ProviderCertification::new);
        certification.setProvider(provider);
        certification.setIdCardFront(request.getIdCardFront());
        certification.setIdCardBack(request.getIdCardBack());
        certification.setHealthCertificate(request.getHealthCertificate());
        certification.setSkillCertificate(request.getSkillCertificate());
        certification.setWorkExperience(request.getWorkExperience());
        certification.setStatus(CertificationStatus.PENDING);
        certification.setAuditRemark(null);
        certification.setAuditTime(null);
        provider.setCertificationStatus(CertificationStatus.PENDING.ordinal());
        provider.setCertificationTime(null);
        providerRepository.save(provider);
        return ApiResponse.success(certificationRepository.save(certification));
    }
}
