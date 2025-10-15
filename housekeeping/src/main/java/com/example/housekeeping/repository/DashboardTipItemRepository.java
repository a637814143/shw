package com.example.housekeeping.repository;

import com.example.housekeeping.entity.DashboardTipItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 居家贴士数据访问
 */
@Repository
public interface DashboardTipItemRepository extends JpaRepository<DashboardTipItem, Long> {
}
