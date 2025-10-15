package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 家政服务实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "housekeeping_service")
public class HousekeepingService extends BaseEntity {
    
    @NotNull(message = "分类ID不能为空")
    @Column(name = "category_id", nullable = false)
    private Long categoryId;
    
    @NotNull(message = "服务者ID不能为空")
    @Column(name = "provider_id", nullable = false)
    private Long providerId;
    
    @NotBlank(message = "服务标题不能为空")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "unit", length = 20)
    private String unit = "次";
    
    @Column(name = "images", columnDefinition = "TEXT")
    private String images; // JSON格式存储图片URL数组
    
    @Column(name = "service_area", length = 200)
    private String serviceArea;
    
    @Column(name = "service_time", length = 100)
    private String serviceTime;
    
    @Column(name = "status")
    private Integer status = 1; // 0-下架，1-上架
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private ServiceCategory category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", insertable = false, updatable = false)
    private Provider provider;
}
