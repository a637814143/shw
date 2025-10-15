package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务评价实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_review")
public class ServiceReview extends BaseEntity {
    
    @NotNull(message = "预约ID不能为空")
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;
    
    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @NotNull(message = "服务ID不能为空")
    @Column(name = "service_id", nullable = false)
    private Long serviceId;
    
    @NotNull(message = "服务者ID不能为空")
    @Column(name = "provider_id", nullable = false)
    private Long providerId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分不能小于1")
    @Max(value = 5, message = "评分不能大于5")
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "images", columnDefinition = "TEXT")
    private String images; // JSON格式存储图片URL数组
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", insertable = false, updatable = false)
    private ServiceBooking booking;
    
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
