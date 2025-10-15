package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 服务预约数据访问层
 */
@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {
    
    /**
     * 根据预约单号查找预约
     */
    Optional<ServiceBooking> findByBookingNo(String bookingNo);
    
    /**
     * 根据用户ID查找预约
     */
    List<ServiceBooking> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 根据服务者ID查找预约
     */
    List<ServiceBooking> findByProviderIdOrderByCreateTimeDesc(Long providerId);
    
    /**
     * 根据条件分页查询预约
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE " +
           "(:bookingNo IS NULL OR sb.bookingNo LIKE %:bookingNo%) AND " +
           "(:userId IS NULL OR sb.userId = :userId) AND " +
           "(:providerId IS NULL OR sb.providerId = :providerId) AND " +
           "(:status IS NULL OR sb.status = :status)")
    Page<ServiceBooking> findByConditions(@Param("bookingNo") String bookingNo,
                                         @Param("userId") Long userId,
                                         @Param("providerId") Long providerId,
                                         @Param("status") Integer status,
                                         Pageable pageable);
    
    /**
     * 统计预约数量
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.status IN (1, 2, 3)")
    long countActiveBookings();
    
    /**
     * 统计待分配预约数量
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.status = 0")
    long countPendingBookings();
    
    /**
     * 查询指定时间范围内的预约数量
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.createTime BETWEEN :startTime AND :endTime")
    long countBookingsByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                 @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定服务者的活跃预约数量
     */
    long countByProviderIdAndStatusIn(Long providerId, List<Integer> statuses);
}
