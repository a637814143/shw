package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepingTip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 居家贴士数据访问层
 */
@Repository
public interface HousekeepingTipRepository extends JpaRepository<HousekeepingTip, Long> {

    /**
     * 根据状态查询贴士
     */
    List<HousekeepingTip> findByStatusOrderByCreateTimeDesc(Integer status);

    /**
     * 根据状态分页查询贴士
     */
    Page<HousekeepingTip> findByStatusOrderByCreateTimeDesc(Integer status, Pageable pageable);

    /**
     * 根据关键词搜索贴士
     */
    @Query("SELECT ht FROM HousekeepingTip ht WHERE ht.status = 1 AND " +
           "(ht.title LIKE %:keyword% OR ht.summary LIKE %:keyword% OR ht.tags LIKE %:keyword%)")
    Page<HousekeepingTip> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询热门贴士（按浏览次数排序）
     */
    @Query("SELECT ht FROM HousekeepingTip ht WHERE ht.status = 1 ORDER BY ht.viewCount DESC")
    Page<HousekeepingTip> findPopularTips(Pageable pageable);

    /**
     * 查询最新贴士
     */
    @Query("SELECT ht FROM HousekeepingTip ht WHERE ht.status = 1 ORDER BY ht.createTime DESC")
    Page<HousekeepingTip> findLatestTips(Pageable pageable);

    /**
     * 根据标签查询贴士
     */
    @Query("SELECT ht FROM HousekeepingTip ht WHERE ht.status = 1 AND ht.tags LIKE %:tag%")
    List<HousekeepingTip> findByTag(@Param("tag") String tag);

    /**
     * 统计贴士数量
     */
    @Query("SELECT COUNT(ht) FROM HousekeepingTip ht WHERE ht.status = 1")
    long countPublishedTips();
}
