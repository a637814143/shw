package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceEvaluationRepository extends JpaRepository<ServiceEvaluation, Long> {

    @Query("SELECT e FROM ServiceEvaluation e WHERE (:keyword IS NULL OR LOWER(e.providerName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.serviceName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.userName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ServiceEvaluation> search(@Param("keyword") String keyword, Pageable pageable);
}
