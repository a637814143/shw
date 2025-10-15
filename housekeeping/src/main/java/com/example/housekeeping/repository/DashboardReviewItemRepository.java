package com.example.housekeeping.repository;

import com.example.housekeeping.entity.DashboardReviewItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 服务评价数据访问
 */
@Repository
public interface DashboardReviewItemRepository extends JpaRepository<DashboardReviewItem, Long> {
}
