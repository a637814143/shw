package com.example.housekeeping.repository;

import com.example.housekeeping.entity.RechargeRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 充值记录数据访问层
 */
@Repository
public interface RechargeRecordRepository extends JpaRepository<RechargeRecord, Long> {

    /**
     * 根据用户ID查询充值记录
     */
    List<RechargeRecord> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 根据用户ID分页查询充值记录
     */
    Page<RechargeRecord> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);

    /**
     * 根据状态查询充值记录
     */
    List<RechargeRecord> findByStatus(Integer status);

    /**
     * 根据状态分页查询充值记录
     */
    Page<RechargeRecord> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据用户ID和状态查询充值记录
     */
    List<RechargeRecord> findByUserIdAndStatus(Long userId, Integer status);

    /**
     * 根据交易单号查询充值记录
     */
    RechargeRecord findByTransactionNo(String transactionNo);

    /**
     * 统计用户充值总金额
     */
    @Query("SELECT SUM(rr.amount) FROM RechargeRecord rr WHERE rr.userId = :userId AND rr.status = 1")
    Double getTotalRechargeAmountByUserId(@Param("userId") Long userId);

    /**
     * 统计用户充值次数
     */
    @Query("SELECT COUNT(rr) FROM RechargeRecord rr WHERE rr.userId = :userId AND rr.status = 1")
    long countSuccessfulRechargesByUserId(@Param("userId") Long userId);

    /**
     * 统计系统充值总金额
     */
    @Query("SELECT SUM(rr.amount) FROM RechargeRecord rr WHERE rr.status = 1")
    Double getTotalRechargeAmount();

    /**
     * 统计一周内的充值金额
     */
    @Query("SELECT SUM(rr.amount) FROM RechargeRecord rr WHERE rr.status = 1 AND rr.createTime >= :startTime")
    Double getWeeklyRechargeAmount(@Param("startTime") LocalDateTime startTime);

    /**
     * 统计一周内的充值次数
     */
    @Query("SELECT COUNT(rr) FROM RechargeRecord rr WHERE rr.status = 1 AND rr.createTime >= :startTime")
    long countWeeklyRecharges(@Param("startTime") LocalDateTime startTime);
}
