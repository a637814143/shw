package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.WithdrawRecord;
import com.example.housekeeping.enums.WithdrawStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface WithdrawRecordRepository extends JpaRepository<WithdrawRecord, Long> {

    Page<WithdrawRecord> findByProvider(ServiceProvider provider, Pageable pageable);

    long countByStatus(WithdrawStatus status);

    Page<WithdrawRecord> findByStatus(WithdrawStatus status, Pageable pageable);

    @Query("select coalesce(sum(w.amount),0) from WithdrawRecord w where (:status is null or w.status = :status)")
    BigDecimal sumAmountByStatus(@org.springframework.data.repository.query.Param("status") WithdrawStatus status);
}
