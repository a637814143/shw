package com.example.housekeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 居家贴士实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "housekeeping_tip")
public class HousekeepingTip extends BaseEntity {
    
    @NotBlank(message = "标题不能为空")
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @NotBlank(message = "内容不能为空")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "status")
    private Integer status = 1; // 0-禁用，1-启用
}
