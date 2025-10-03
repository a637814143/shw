package com.example.housekeeping.repository;

import com.example.housekeeping.entity.HousekeepingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 家政服务数据访问层
 */
@Repository
public interface HousekeepingServiceRepository extends JpaRepository<HousekeepingService, Long> {

    /**
     * 根据分类ID查询服务
     */
    List<HousekeepingService> findByCategoryIdAndStatus(Long categoryId, Integer status);

    /**
     * 根据分类ID分页查询服务
     */
    Page<HousekeepingService> findByCategoryIdAndStatus(Long categoryId, Integer status, Pageable pageable);

    /**
     * 根据服务者ID查询服务
     */
    List<HousekeepingService> findByProviderIdAndStatus(Long providerId, Integer status);

    /**
     * 根据服务者ID分页查询服务
     */
    Page<HousekeepingService> findByProviderIdAndStatus(Long providerId, Integer status, Pageable pageable);

    /**
     * 根据状态查询服务
     */
    List<HousekeepingService> findByStatus(Integer status);

    /**
     * 根据状态分页查询服务
     */
    Page<HousekeepingService> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据价格范围查询服务
     */
    @Query("SELECT hs FROM HousekeepingService hs WHERE hs.status = 1 AND hs.price BETWEEN :minPrice AND :maxPrice")
    Page<HousekeepingService> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                               @Param("maxPrice") BigDecimal maxPrice, 
                                               Pageable pageable);

    /**
     * 根据关键词搜索服务
     */
    @Query("SELECT hs FROM HousekeepingService hs WHERE hs.status = 1 AND " +
           "(hs.name LIKE %:keyword% OR hs.description LIKE %:keyword% OR hs.tags LIKE %:keyword%)")
    Page<HousekeepingService> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询热门服务（按预约数量排序）
     */
    @Query("SELECT hs FROM HousekeepingService hs WHERE hs.status = 1 ORDER BY hs.bookingCount DESC")
    Page<HousekeepingService> findPopularServices(Pageable pageable);

    /**
     * 查询高评分服务
     */
    @Query("SELECT hs FROM HousekeepingService hs WHERE hs.status = 1 AND hs.rating >= :minRating ORDER BY hs.rating DESC")
    Page<HousekeepingService> findHighRatedServices(@Param("minRating") BigDecimal minRating, Pageable pageable);

    /**
     * 统计服务数量
     */
    @Query("SELECT COUNT(hs) FROM HousekeepingService hs WHERE hs.status = 1")
    long countActiveServices();

    /**
     * 统计分类下的服务数量
     */
    @Query("SELECT COUNT(hs) FROM HousekeepingService hs WHERE hs.categoryId = :categoryId AND hs.status = 1")
    long countByCategoryId(@Param("categoryId") Long categoryId);
}
