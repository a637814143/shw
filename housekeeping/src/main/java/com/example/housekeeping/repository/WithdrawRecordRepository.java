package com.example.housekeeping.repository;

import com.example.housekeeping.entity.WithdrawRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提现记录数据访问层
 */
@Repository
public interface WithdrawRecordRepository extends JpaRepository<WithdrawRecord, Long> {

    /**
     * 根据服务者ID查询提现记录
     */
    List<WithdrawRecord> findByProviderIdOrderByCreateTimeDesc(Long providerId);

    /**
     * 根据服务者ID分页查询提现记录
     */
    Page<WithdrawRecord> findByProviderIdOrderByCreateTimeDesc(Long providerId, Pageable pageable);

    /**
     * 根据状态查询提现记录
     */
    List<WithdrawRecord> findByStatus(Integer status);

    /**
     * 根据状态分页查询提现记录
     */
    Page<WithdrawRecord> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据服务者ID和状态查询提现记录
     */
    List<WithdrawRecord> findByProviderIdAndStatus(Long providerId, Integer status);

    /**
     * 统计服务者提现总金额
     */
    @Query("SELECT SUM(wr.amount) FROM WithdrawRecord wr WHERE wr.providerId = :providerId AND wr.status = 1")
    Double getTotalWithdrawAmountByProviderId(@Param("providerId") Long providerId);

    /**
     * 统计服务者提现次数
     */
    @Query("SELECT COUNT(wr) FROM WithdrawRecord wr WHERE wr.providerId = :providerId AND wr.status = 1")
    long countSuccessfulWithdrawsByProviderId(@Param("providerId") Long providerId);

    /**
     * 统计系统提现总金额
     */
    @Query("SELECT SUM(wr.amount) FROM WithdrawRecord wr WHERE wr.status = 1")
    Double getTotalWithdrawAmount();

    /**
     * 统计待审核的提现数量
     */
    @Query("SELECT COUNT(wr) FROM WithdrawRecord wr WHERE wr.status = 0")
    long countPendingWithdraws();

    /**
     * 统计一周内的提现金额
     */
    @Query("SELECT SUM(wr.amount) FROM WithdrawRecord wr WHERE wr.status = 1 AND wr.createTime >= :startTime")
    Double getWeeklyWithdrawAmount(@Param("startTime") LocalDateTime startTime);

    /**
     * 统计一周内的提现次数
     */
    @Query("SELECT COUNT(wr) FROM WithdrawRecord wr WHERE wr.status = 1 AND wr.createTime >= :startTime")
    long countWeeklyWithdraws(@Param("startTime") LocalDateTime startTime);
}
