package com.example.housekeeping.repository;

import com.example.housekeeping.entity.ServiceBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
     * 根据用户ID查询预约
     */
    List<ServiceBooking> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 根据用户ID分页查询预约
     */
    Page<ServiceBooking> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据服务者ID查询预约
     */
    List<ServiceBooking> findByProviderIdOrderByCreateTimeDesc(Long providerId);

    /**
     * 根据服务者ID分页查询预约
     */
    Page<ServiceBooking> findByProviderIdOrderByCreateTimeDesc(Long providerId, Pageable pageable);

    /**
     * 根据服务ID查询预约
     */
    List<ServiceBooking> findByServiceIdOrderByCreateTimeDesc(Long serviceId);

    /**
     * 根据状态查询预约
     */
    List<ServiceBooking> findByStatus(Integer status);

    /**
     * 根据状态分页查询预约
     */
    Page<ServiceBooking> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据用户ID和状态查询预约
     */
    List<ServiceBooking> findByUserIdAndStatus(Long userId, Integer status);

    /**
     * 根据服务者ID和状态查询预约
     */
    List<ServiceBooking> findByProviderIdAndStatus(Long providerId, Integer status);

    /**
     * 根据支付状态查询预约
     */
    List<ServiceBooking> findByPaymentStatus(Integer paymentStatus);

    /**
     * 根据预约日期查询预约
     */
    List<ServiceBooking> findByBookingDate(LocalDate bookingDate);

    /**
     * 根据预约日期范围查询预约
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.bookingDate BETWEEN :startDate AND :endDate")
    List<ServiceBooking> findByBookingDateRange(@Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);

    /**
     * 统计预约数量
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.status = :status")
    long countByStatus(@Param("status") Integer status);

    /**
     * 统计用户预约数量
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    /**
     * 统计服务者预约数量
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.providerId = :providerId")
    long countByProviderId(@Param("providerId") Long providerId);

    /**
     * 查询今日预约
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.bookingDate = :date")
    List<ServiceBooking> findTodayBookings(@Param("date") LocalDate date);

    /**
     * 查询待分配的预约
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.status = 0 ORDER BY sb.createTime ASC")
    List<ServiceBooking> findPendingBookings();

    /**
     * 统计一周内的预约数量
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.createTime >= :startTime")
    long countWeeklyBookings(@Param("startTime") LocalDateTime startTime);
}
