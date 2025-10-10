package com.example.housekeeping.controller;

import com.example.housekeeping.dto.ProviderCertificationRequest;
import com.example.housekeeping.dto.ProviderReviewRequest;
import com.example.housekeeping.entity.ProviderCertification;
import com.example.housekeeping.entity.ProviderCertificationStatus;
import com.example.housekeeping.exception.ResourceNotFoundException;
import com.example.housekeeping.repository.ProviderCertificationRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/provider-certifications")
public class ProviderCertificationController {

    private final ProviderCertificationRepository certificationRepository;

    public ProviderCertificationController(ProviderCertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }

    @GetMapping
    public Page<ProviderCertification> list(@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) ProviderCertificationStatus status,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("appliedAt").descending());
        String normalizedKeyword = keyword != null && !keyword.isBlank() ? keyword : null;
        return certificationRepository.search(normalizedKeyword, status, pageable);
    }

    @GetMapping("/{id}")
    public ProviderCertification detail(@PathVariable Long id) {
        return certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的认证信息"));
    }

    @PostMapping
    public ResponseEntity<ProviderCertification> create(@Valid @RequestBody ProviderCertificationRequest request) {
        ProviderCertification certification = ProviderCertification.builder()
                .name(request.getName())
                .certificateUrl(request.getCertificateUrl())
                .idCardUrl(request.getIdCardUrl())
                .idCardNumber(request.getIdCardNumber())
                .contact(request.getContact())
                .address(request.getAddress())
                .status(ProviderCertificationStatus.PENDING)
                .reviewComment(null)
                .appliedAt(request.getAppliedAt())
                .reviewedAt(null)
                .build();
        ProviderCertification saved = certificationRepository.save(certification);
        return ResponseEntity.created(URI.create("/api/provider-certifications/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ProviderCertification update(@PathVariable Long id, @Valid @RequestBody ProviderCertificationRequest request) {
        ProviderCertification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的认证信息"));
        certification.setName(request.getName());
        certification.setCertificateUrl(request.getCertificateUrl());
        certification.setIdCardUrl(request.getIdCardUrl());
        certification.setIdCardNumber(request.getIdCardNumber());
        certification.setContact(request.getContact());
        certification.setAddress(request.getAddress());
        certification.setAppliedAt(request.getAppliedAt());
        return certificationRepository.save(certification);
    }

    @PostMapping("/{id}/review")
    public ProviderCertification review(@PathVariable Long id, @Valid @RequestBody ProviderReviewRequest request) {
        ProviderCertification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的认证信息"));
        certification.setStatus(request.getStatus());
        certification.setReviewComment(request.getReviewComment());
        certification.setReviewedAt(request.getReviewedAt() != null ? request.getReviewedAt() : LocalDateTime.now());
        return certificationRepository.save(certification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ProviderCertification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的认证信息"));
        certificationRepository.delete(certification);
        return ResponseEntity.noContent().build();
    }
}
