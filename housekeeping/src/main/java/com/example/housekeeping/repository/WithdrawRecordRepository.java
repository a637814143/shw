package com.example.housekeeping.repository;

import com.example.housekeeping.entity.WithdrawRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 提现记录数据访问层
 */
@Repository
public interface WithdrawRecordRepository extends JpaRepository<WithdrawRecord, Long> {
    
    /**
     * 根据服务者ID查找提现记录
     */
    List<WithdrawRecord> findByProviderIdOrderByCreateTimeDesc(Long providerId);
    
    /**
     * 根据条件分页查询提现记录
     */
    @Query("SELECT wr FROM WithdrawRecord wr WHERE " +
           "(:providerId IS NULL OR wr.providerId = :providerId) AND " +
           "(:status IS NULL OR wr.status = :status)")
    Page<WithdrawRecord> findByConditions(@Param("providerId") Long providerId,
                                         @Param("status") Integer status,
                                         Pageable pageable);
    
    /**
     * 统计提现总金额
     */
    @Query("SELECT COALESCE(SUM(wr.amount), 0) FROM WithdrawRecord wr WHERE wr.status = 1")
    BigDecimal sumTotalWithdrawAmount();
    
    /**
     * 统计待审核提现数量
     */
    @Query("SELECT COUNT(wr) FROM WithdrawRecord wr WHERE wr.status = 0")
    long countPendingWithdraws();
}
