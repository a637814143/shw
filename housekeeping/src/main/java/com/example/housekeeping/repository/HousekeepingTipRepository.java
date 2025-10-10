package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepingTip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HousekeepingTipRepository extends JpaRepository<HousekeepingTip, Long> {

    @Query("SELECT t FROM HousekeepingTip t WHERE (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.intro) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<HousekeepingTip> search(@Param("keyword") String keyword, Pageable pageable);
}
