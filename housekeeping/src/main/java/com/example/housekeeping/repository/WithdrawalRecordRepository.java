package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.WithdrawalRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRecordRepository extends JpaRepository<WithdrawalRecord, Long> {
    List<WithdrawalRecord> findByProviderIdOrderByCreatedAtDesc(Long providerId);
}
