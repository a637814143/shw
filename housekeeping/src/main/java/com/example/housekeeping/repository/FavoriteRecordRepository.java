package com.example.housekeeping.repository;

import com.example.housekeeping.entity.FavoriteRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRecordRepository extends JpaRepository<FavoriteRecord, Long> {

    @Query("SELECT f FROM FavoriteRecord f WHERE (:keyword IS NULL OR LOWER(f.serviceName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(f.userName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<FavoriteRecord> search(@Param("keyword") String keyword, Pageable pageable);
}
