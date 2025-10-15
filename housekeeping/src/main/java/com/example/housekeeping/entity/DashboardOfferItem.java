package com.example.housekeeping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户端特惠信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dashboard_offer_item")
public class DashboardOfferItem extends BaseEntity {

    @NotBlank(message = "特惠标题不能为空")
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotBlank(message = "特惠内容不能为空")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "tag", length = 50)
    private String tag;
}
