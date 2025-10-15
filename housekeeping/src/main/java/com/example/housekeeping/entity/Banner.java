package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 轮播图实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "banner")
public class Banner extends BaseEntity {
    
    @NotBlank(message = "标题不能为空")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @NotBlank(message = "图片URL不能为空")
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    @Column(name = "link_url")
    private String linkUrl;
    
    @Column(name = "service_id")
    private Long serviceId;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用，1-启用
    
    // 关联关系
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", insertable = false, updatable = false)
    private HousekeepingService service;
}
