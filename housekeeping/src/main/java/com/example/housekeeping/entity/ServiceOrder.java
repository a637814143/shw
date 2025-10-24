package com.example.housekeeping.entity;

import com.example.housekeeping.enums.ServiceOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 用户选择的家政服务订单。
 */
@Getter
@Setter
@Entity
@Table(name = "service_order")
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private HousekeepService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAll user;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ServiceOrderStatus status;

    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Column(name = "special_request", length = 500)
    private String specialRequest;

    @Column(name = "service_address", length = 255)
    private String serviceAddress;

    @Column(name = "progress_note", length = 500)
    private String progressNote;

    @Column(name = "loyalty_points", nullable = false)
    private Integer loyaltyPoints;

    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    @Column(name = "refund_response", length = 500)
    private String refundResponse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by")
    private UserAll handledBy;

    @Column(name = "assigned_worker", length = 100)
    private String assignedWorker;

    @Column(name = "worker_contact", length = 100)
    private String workerContact;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "settlement_released", nullable = false)
    private boolean settlementReleased = false;

    @Column(name = "settlement_released_at")
    private Instant settlementReleasedAt;
}
