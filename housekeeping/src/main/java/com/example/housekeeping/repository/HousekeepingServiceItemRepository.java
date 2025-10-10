package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepingServiceItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HousekeepingServiceItemRepository extends JpaRepository<HousekeepingServiceItem, Long> {

    @Query("SELECT s FROM HousekeepingServiceItem s WHERE (:keyword IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR s.category.id = :categoryId)")
    Page<HousekeepingServiceItem> search(@Param("keyword") String keyword,
                                         @Param("categoryId") Long categoryId,
                                         Pageable pageable);
}
