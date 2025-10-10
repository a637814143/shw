package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ProviderCertification;
import com.example.housekeeping.entity.ProviderCertificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderCertificationRepository extends JpaRepository<ProviderCertification, Long> {

    @Query("SELECT p FROM ProviderCertification p WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR p.status = :status)")
    Page<ProviderCertification> search(@Param("keyword") String keyword,
                                       @Param("status") ProviderCertificationStatus status,
                                       Pageable pageable);

    long countByStatus(ProviderCertificationStatus status);
}
