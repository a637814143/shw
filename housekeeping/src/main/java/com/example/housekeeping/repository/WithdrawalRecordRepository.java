package com.example.housekeeping.repository;

import com.example.housekeeping.entity.WithdrawalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface WithdrawalRecordRepository extends JpaRepository<WithdrawalRecord, Long> {

    @Query("SELECT w FROM WithdrawalRecord w WHERE (:providerName IS NULL OR LOWER(w.providerName) LIKE LOWER(CONCAT('%', :providerName, '%'))) " +
            "AND (:date IS NULL OR DATE(w.withdrawalTime) = :date)")
    Page<WithdrawalRecord> search(@Param("providerName") String providerName,
                                  @Param("date") LocalDate date,
                                  Pageable pageable);

    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM WithdrawalRecord w")
    java.math.BigDecimal totalAmount();
}
