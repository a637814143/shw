package com.example.housekeeping.entity;

import com.example.housekeeping.enums.CertificationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "provider_certification")
@Getter
@Setter
public class ProviderCertification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    @JsonIgnore
    private ServiceProvider provider;

    @Column(name = "id_card_front")
    private String idCardFront;

    @Column(name = "id_card_back")
    private String idCardBack;

    @Column(name = "health_certificate")
    private String healthCertificate;

    @Column(name = "skill_certificate")
    private String skillCertificate;

    @Column(name = "work_experience", columnDefinition = "TEXT")
    private String workExperience;

    @Enumerated(EnumType.STRING)
    private CertificationStatus status;

    @Column(name = "audit_remark")
    private String auditRemark;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;
}
