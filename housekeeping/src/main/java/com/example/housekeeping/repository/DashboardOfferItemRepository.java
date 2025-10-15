package com.example.housekeeping.repository;

import com.example.housekeeping.entity.DashboardOfferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 特惠信息数据访问
 */
@Repository
public interface DashboardOfferItemRepository extends JpaRepository<DashboardOfferItem, Long> {
}
