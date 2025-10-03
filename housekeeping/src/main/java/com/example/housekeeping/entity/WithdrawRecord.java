package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 提现记录实体类
 */
@Data
@Entity
@Table(name = "withdraw_record")
@EqualsAndHashCode(callSuper = true)
public class WithdrawRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(name = "account_holder", length = 50)
    private String accountHolder;

    @Column(name = "status", columnDefinition = "tinyint default 0")
    private Integer status = 0; // 0-待审核，1-已通过，2-已拒绝

    @Column(name = "remark")
    private String remark;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private ServiceProvider provider;
}
