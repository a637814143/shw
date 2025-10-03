package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 服务者认证材料实体类
 */
@Data
@Entity
@Table(name = "provider_certification")
@EqualsAndHashCode(callSuper = true)
public class ProviderCertification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false, unique = true)
    private Long providerId;

    @Column(name = "id_card_front")
    private String idCardFront;

    @Column(name = "id_card_back")
    private String idCardBack;

    @Column(name = "health_certificate")
    private String healthCertificate;

    @Column(name = "skill_certificate")
    private String skillCertificate;

    @Column(name = "work_experience", columnDefinition = "text")
    private String workExperience;

    @Column(name = "status", columnDefinition = "tinyint default 0")
    private Integer status = 0; // 0-待审核，1-已通过，2-已拒绝

    @Column(name = "audit_remark")
    private String auditRemark;

    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    // 关联关系
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private ServiceProvider provider;
}
