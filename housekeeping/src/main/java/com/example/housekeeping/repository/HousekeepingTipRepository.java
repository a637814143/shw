package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepingTip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HousekeepingTipRepository extends JpaRepository<HousekeepingTip, Long> {
}
