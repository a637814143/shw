package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.ProviderCertification;
import com.example.housekeeping.domain.enums.CertificationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderCertificationRepository extends JpaRepository<ProviderCertification, Long> {
    List<ProviderCertification> findByStatus(CertificationStatus status);
    Optional<ProviderCertification> findTopByProviderIdOrderByCreatedAtDesc(Long providerId);
    long countByStatus(CertificationStatus status);
}
