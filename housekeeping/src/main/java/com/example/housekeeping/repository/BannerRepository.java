package com.example.housekeeping.repository;

import com.example.housekeeping.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 轮播图数据访问层
 */
@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    /**
     * 根据状态查询轮播图
     */
    List<Banner> findByStatusOrderBySortOrder(Integer status);

    /**
     * 查询所有启用的轮播图，按排序字段排序
     */
    @Query("SELECT b FROM Banner b WHERE b.status = 1 ORDER BY b.sortOrder ASC, b.id ASC")
    List<Banner> findAllEnabledOrderBySortOrder();
}
