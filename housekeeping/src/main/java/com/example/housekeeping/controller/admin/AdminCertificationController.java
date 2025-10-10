package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.CertificationAuditRequest;
import com.example.housekeeping.entity.ProviderCertification;
import com.example.housekeeping.enums.CertificationStatus;
import com.example.housekeeping.repository.ProviderCertificationRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/certifications")
@RequiredArgsConstructor
public class AdminCertificationController {

    private final ProviderCertificationRepository certificationRepository;
    private final ServiceProviderRepository providerRepository;

    @GetMapping
    public ApiResponse<Page<ProviderCertification>> list(@RequestParam(required = false) CertificationStatus status,
                                                         Pageable pageable) {
        Page<ProviderCertification> page = status == null
                ? certificationRepository.findAll(pageable)
                : certificationRepository.findByStatus(status, pageable);
        return ApiResponse.success(page);
    }

    @PutMapping("/{id}/audit")
    public ApiResponse<ProviderCertification> audit(@PathVariable Long id,
                                                    @RequestBody @Valid CertificationAuditRequest request) {
        ProviderCertification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("认证信息不存在"));
        certification.setStatus(request.getStatus());
        certification.setAuditRemark(request.getRemark());
        certification.setAuditTime(LocalDateTime.now());
        certificationRepository.save(certification);
        certification.getProvider().setCertificationStatus(request.getStatus().ordinal());
        if (request.getStatus() == CertificationStatus.APPROVED) {
            certification.getProvider().setCertificationTime(LocalDateTime.now());
        } else {
            certification.getProvider().setCertificationTime(null);
        }
        providerRepository.save(certification.getProvider());
        return ApiResponse.success(certification);
    }
}
