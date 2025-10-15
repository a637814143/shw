package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 提现记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "withdraw_record")
public class WithdrawRecord extends BaseEntity {
    
    @NotNull(message = "服务者ID不能为空")
    @Column(name = "provider_id", nullable = false)
    private Long providerId;
    
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01", message = "提现金额必须大于0")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "bank_name", length = 100)
    private String bankName;
    
    @Column(name = "bank_account", length = 50)
    private String bankAccount;
    
    @Column(name = "account_holder", length = 50)
    private String accountHolder;
    
    @Column(name = "status")
    private Integer status = 0; // 0-待审核，1-已通过，2-已拒绝
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Provider provider;
}
