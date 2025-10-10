package com.example.housekeeping.repository;

import com.example.housekeeping.entity.RechargeRecord;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.enums.RechargeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface RechargeRecordRepository extends JpaRepository<RechargeRecord, Long> {

    Page<RechargeRecord> findByUser(User user, Pageable pageable);

    long countByStatus(RechargeStatus status);

    Page<RechargeRecord> findByStatus(RechargeStatus status, Pageable pageable);

    @Query("select coalesce(sum(r.amount),0) from RechargeRecord r where (:status is null or r.status = :status)")
    BigDecimal sumAmountByStatus(@org.springframework.data.repository.query.Param("status") RechargeStatus status);
}
