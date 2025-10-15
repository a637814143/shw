package com.example.housekeeping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户端服务评价
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dashboard_review_item")
public class DashboardReviewItem extends BaseEntity {

    @NotBlank(message = "评价人不能为空")
    @Column(name = "author", nullable = false, length = 50)
    private String author;

    @NotBlank(message = "服务名称不能为空")
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Min(value = 1, message = "评分必须在1到5之间")
    @Max(value = 5, message = "评分必须在1到5之间")
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @NotBlank(message = "评价内容不能为空")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
