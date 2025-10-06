package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.HousekeepingService;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HousekeepingServiceRepository extends JpaRepository<HousekeepingService, Long> {
    List<HousekeepingService> findByCategoryId(Long categoryId);
    List<HousekeepingService> findByFeaturedTrueOrderByOrderCountDesc();
}
