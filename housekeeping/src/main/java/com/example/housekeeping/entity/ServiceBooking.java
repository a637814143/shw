package com.example.housekeeping.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 服务预约实体类
 */
@Data
@Entity
@Table(name = "service_booking")
@EqualsAndHashCode(callSuper = true)
public class ServiceBooking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_no", nullable = false, unique = true, length = 32)
    private String bookingNo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "provider_id", nullable = false)
    private Long providerId;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "booking_time", nullable = false)
    private LocalTime bookingTime;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    @Column(name = "contact_name", nullable = false, length = 50)
    private String contactName;

    @Column(name = "remark", columnDefinition = "text")
    private String remark;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", columnDefinition = "tinyint default 0")
    private Integer status = 0; // 0-待分配，1-已分配，2-进行中，3-已完成，4-已取消

    @Column(name = "payment_status", columnDefinition = "tinyint default 0")
    private Integer paymentStatus = 0; // 0-未支付，1-已支付，2-已退款

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private HousekeepingService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private ServiceProvider provider;
}
