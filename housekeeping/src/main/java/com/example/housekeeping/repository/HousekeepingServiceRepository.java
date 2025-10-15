package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 家政服务数据访问层
 */
@Repository
public interface HousekeepingServiceRepository extends JpaRepository<HousekeepingService, Long> {
    
    /**
     * 根据分类ID查找服务
     */
    List<HousekeepingService> findByCategoryIdAndStatus(Long categoryId, Integer status);
    
    /**
     * 根据服务者ID查找服务
     */
    List<HousekeepingService> findByProviderIdAndStatus(Long providerId, Integer status);
    
    /**
     * 根据条件分页查询服务
     */
    @Query("SELECT hs FROM HousekeepingService hs WHERE " +
           "(:title IS NULL OR hs.title LIKE %:title%) AND " +
           "(:categoryId IS NULL OR hs.categoryId = :categoryId) AND " +
           "(:providerId IS NULL OR hs.providerId = :providerId) AND " +
           "(:status IS NULL OR hs.status = :status)")
    Page<HousekeepingService> findByConditions(@Param("title") String title,
                                              @Param("categoryId") Long categoryId,
                                              @Param("providerId") Long providerId,
                                              @Param("status") Integer status,
                                              Pageable pageable);
    
    /**
     * 统计服务数量
     */
    @Query("SELECT COUNT(hs) FROM HousekeepingService hs WHERE hs.status = 1")
    long countActiveServices();
}
