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
     * 根据状态查找贴士
     */
    List<HousekeepingTip> findByStatusOrderByCreateTimeDesc(Integer status);
    
    /**
     * 根据分类查找贴士
     */
    List<HousekeepingTip> findByCategoryAndStatusOrderByCreateTimeDesc(String category, Integer status);
    
    /**
     * 根据条件分页查询贴士
     */
    @Query("SELECT ht FROM HousekeepingTip ht WHERE " +
           "(:title IS NULL OR ht.title LIKE %:title%) AND " +
           "(:category IS NULL OR ht.category = :category) AND " +
           "(:status IS NULL OR ht.status = :status)")
    Page<HousekeepingTip> findByConditions(@Param("title") String title,
                                          @Param("category") String category,
                                          @Param("status") Integer status,
                                          Pageable pageable);
}
