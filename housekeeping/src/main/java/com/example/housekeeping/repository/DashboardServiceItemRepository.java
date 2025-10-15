package com.example.housekeeping.repository;

import com.example.housekeeping.entity.DashboardServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 服务条目数据访问
 */
@Repository
public interface DashboardServiceItemRepository extends JpaRepository<DashboardServiceItem, Long> {
}
