package com.example.housekeeping.repository;

import com.example.housekeeping.entity.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 轮播图数据访问层
 */
@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    
    /**
     * 根据状态查找轮播图，按排序字段排序
     */
    List<Banner> findByStatusOrderBySortOrderAsc(Integer status);
    
    /**
     * 根据条件分页查询轮播图
     */
    @Query("SELECT b FROM Banner b WHERE " +
           "(:title IS NULL OR b.title LIKE %:title%) AND " +
           "(:status IS NULL OR b.status = :status)")
    Page<Banner> findByConditions(@Param("title") String title,
                                 @Param("status") Integer status,
                                 Pageable pageable);
}
