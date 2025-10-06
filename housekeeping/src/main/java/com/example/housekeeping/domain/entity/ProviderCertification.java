package com.example.housekeeping.domain.entity;

import com.example.housekeeping.domain.enums.CertificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "provider_certifications")
public class ProviderCertification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private ServiceProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificationStatus status = CertificationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String documents;

    private String experienceYears;

    private String speciality;

    private String auditRemark;

    private String reviewedBy;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;
}
