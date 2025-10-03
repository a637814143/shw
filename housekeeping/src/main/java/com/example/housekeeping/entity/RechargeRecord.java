package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 充值记录实体类
 */
@Data
@Entity
@Table(name = "recharge_record")
@EqualsAndHashCode(callSuper = true)
public class RechargeRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod;

    @Column(name = "transaction_no", length = 64)
    private String transactionNo;

    @Column(name = "status", columnDefinition = "tinyint default 0")
    private Integer status = 0; // 0-待支付，1-已支付，2-支付失败

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
