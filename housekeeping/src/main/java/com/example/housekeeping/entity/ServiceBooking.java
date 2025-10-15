package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 服务预约实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_booking")
public class ServiceBooking extends BaseEntity {
    
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @NotNull(message = "服务ID不能为空")
    @Column(name = "service_id", nullable = false)
    private Long serviceId;
    
    @Column(name = "provider_id")
    private Long providerId;
    
    @NotBlank(message = "预约单号不能为空")
    @Column(name = "booking_no", unique = true, nullable = false, length = 50)
    private String bookingNo;
    
    @NotNull(message = "预约服务时间不能为空")
    @Column(name = "service_time", nullable = false)
    private LocalDateTime serviceTime;
    
    @NotBlank(message = "服务地址不能为空")
    @Column(name = "address", nullable = false, length = 500)
    private String address;
    
    @NotBlank(message = "联系人姓名不能为空")
    @Column(name = "contact_name", nullable = false, length = 50)
    private String contactName;
    
    @NotBlank(message = "联系人电话不能为空")
    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
    @NotNull(message = "总金额不能为空")
    @DecimalMin(value = "0.01", message = "总金额必须大于0")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(name = "status")
    private Integer status = 0; // 0-待分配，1-已分配，2-服务中，3-已完成，4-已取消
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private HousekeepingService service;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Provider provider;
}
