package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 服务评价数据访问层
 */
@Repository
public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Long> {
    
    /**
     * 根据预约ID查找评价
     */
    ServiceReview findByBookingId(Long bookingId);
    
    /**
     * 根据服务ID查找评价
     */
    List<ServiceReview> findByServiceIdOrderByCreateTimeDesc(Long serviceId);
    
    /**
     * 根据服务者ID查找评价
     */
    List<ServiceReview> findByProviderIdOrderByCreateTimeDesc(Long providerId);
    
    /**
     * 根据条件分页查询评价
     */
    @Query("SELECT sr FROM ServiceReview sr WHERE " +
           "(:serviceId IS NULL OR sr.serviceId = :serviceId) AND " +
           "(:providerId IS NULL OR sr.providerId = :providerId) AND " +
           "(:userId IS NULL OR sr.userId = :userId)")
    Page<ServiceReview> findByConditions(@Param("serviceId") Long serviceId,
                                        @Param("providerId") Long providerId,
                                        @Param("userId") Long userId,
                                        Pageable pageable);
    
    /**
     * 计算服务平均评分
     */
    @Query("SELECT COALESCE(AVG(sr.rating), 0) FROM ServiceReview sr WHERE sr.serviceId = :serviceId")
    Double getAverageRatingByServiceId(@Param("serviceId") Long serviceId);
    
    /**
     * 计算服务者平均评分
     */
    @Query("SELECT COALESCE(AVG(sr.rating), 0) FROM ServiceReview sr WHERE sr.providerId = :providerId")
    Double getAverageRatingByProviderId(@Param("providerId") Long providerId);
}
