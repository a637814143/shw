package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 服务分类实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "service_category")
public class ServiceCategory extends BaseEntity {
    
    @NotBlank(message = "分类名称不能为空")
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "icon")
    private String icon;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用，1-启用
}
