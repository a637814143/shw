package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 服务评价数据访问层
 */
@Repository
public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Long> {

    /**
     * 根据预约ID查找评价
     */
    Optional<ServiceReview> findByBookingId(Long bookingId);

    /**
     * 根据服务ID查询评价
     */
    List<ServiceReview> findByServiceIdOrderByCreateTimeDesc(Long serviceId);

    /**
     * 根据服务ID分页查询评价
     */
    Page<ServiceReview> findByServiceIdOrderByCreateTimeDesc(Long serviceId, Pageable pageable);

    /**
     * 根据服务者ID查询评价
     */
    List<ServiceReview> findByProviderIdOrderByCreateTimeDesc(Long providerId);

    /**
     * 根据服务者ID分页查询评价
     */
    Page<ServiceReview> findByProviderIdOrderByCreateTimeDesc(Long providerId, Pageable pageable);

    /**
     * 根据用户ID查询评价
     */
    List<ServiceReview> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 根据用户ID分页查询评价
     */
    Page<ServiceReview> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据评分查询评价
     */
    List<ServiceReview> findByRating(Integer rating);

    /**
     * 根据服务ID和评分查询评价
     */
    List<ServiceReview> findByServiceIdAndRating(Long serviceId, Integer rating);

    /**
     * 统计服务评价数量
     */
    @Query("SELECT COUNT(sr) FROM ServiceReview sr WHERE sr.serviceId = :serviceId")
    long countByServiceId(@Param("serviceId") Long serviceId);

    /**
     * 统计服务者评价数量
     */
    @Query("SELECT COUNT(sr) FROM ServiceReview sr WHERE sr.providerId = :providerId")
    long countByProviderId(@Param("providerId") Long providerId);

    /**
     * 计算服务平均评分
     */
    @Query("SELECT AVG(sr.rating) FROM ServiceReview sr WHERE sr.serviceId = :serviceId")
    Double getAverageRatingByServiceId(@Param("serviceId") Long serviceId);

    /**
     * 计算服务者平均评分
     */
    @Query("SELECT AVG(sr.rating) FROM ServiceReview sr WHERE sr.providerId = :providerId")
    Double getAverageRatingByProviderId(@Param("providerId") Long providerId);

    /**
     * 统计各评分数量
     */
    @Query("SELECT sr.rating, COUNT(sr) FROM ServiceReview sr WHERE sr.serviceId = :serviceId GROUP BY sr.rating")
    List<Object[]> countRatingDistributionByServiceId(@Param("serviceId") Long serviceId);
}
