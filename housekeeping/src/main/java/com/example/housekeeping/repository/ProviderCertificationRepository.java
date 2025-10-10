package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ProviderCertification;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.enums.CertificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderCertificationRepository extends JpaRepository<ProviderCertification, Long> {

    Optional<ProviderCertification> findByProvider(ServiceProvider provider);

    long countByStatus(CertificationStatus status);

    org.springframework.data.domain.Page<ProviderCertification> findByStatus(CertificationStatus status, org.springframework.data.domain.Pageable pageable);
}
