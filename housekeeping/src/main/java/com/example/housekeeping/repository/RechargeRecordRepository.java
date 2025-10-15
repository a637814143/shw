package com.example.housekeeping.repository;

import com.example.housekeeping.entity.RechargeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 充值记录数据访问层
 */
@Repository
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord, Long> {
    
    /**
     * 根据用户ID查找充值记录
     */
    List<RechargeRecord> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 根据条件分页查询充值记录
     */
    @Query("SELECT rr FROM RechargeRecord rr WHERE " +
           "(:userId IS NULL OR rr.userId = :userId) AND " +
           "(:status IS NULL OR rr.status = :status)")
    Page<RechargeRecord> findByConditions(@Param("userId") Long userId,
                                         @Param("status") Integer status,
                                         Pageable pageable);
    
    /**
     * 统计充值总金额
     */
    @Query("SELECT COALESCE(SUM(rr.amount), 0) FROM RechargeRecord rr WHERE rr.status = 1")
    BigDecimal sumTotalRechargeAmount();
    
    /**
     * 查询指定时间范围内的充值金额
     */
    @Query("SELECT COALESCE(SUM(rr.amount), 0) FROM RechargeRecord rr WHERE " +
           "rr.status = 1 AND rr.createTime BETWEEN :startTime AND :endTime")
    BigDecimal sumRechargeAmountByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查询一周充值趋势数据
     */
    @Query("SELECT DATE(rr.createTime) as date, COALESCE(SUM(rr.amount), 0) as amount " +
           "FROM RechargeRecord rr WHERE rr.status = 1 AND rr.createTime >= :startTime " +
           "GROUP BY DATE(rr.createTime) ORDER BY date")
    List<Object[]> getWeeklyRechargeTrend(@Param("startTime") LocalDateTime startTime);
}
