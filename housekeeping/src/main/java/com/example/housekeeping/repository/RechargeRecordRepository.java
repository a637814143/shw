package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.RechargeRecord;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RechargeRecordRepository extends JpaRepository<RechargeRecord, Long> {
    List<RechargeRecord> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<RechargeRecord> findByPaidAtBetween(LocalDateTime start, LocalDateTime end);
}
