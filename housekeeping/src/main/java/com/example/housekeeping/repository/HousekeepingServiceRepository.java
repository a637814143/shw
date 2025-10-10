package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepingService;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.entity.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HousekeepingServiceRepository extends JpaRepository<HousekeepingService, Long> {

    Page<HousekeepingService> findByCategory(ServiceCategory category, Pageable pageable);

    Page<HousekeepingService> findByProvider(ServiceProvider provider, Pageable pageable);

    Page<HousekeepingService> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
