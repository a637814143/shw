package com.example.housekeeping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户端居家小贴士
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dashboard_tip_item")
public class DashboardTipItem extends BaseEntity {

    @NotBlank(message = "贴士标题不能为空")
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotBlank(message = "贴士内容不能为空")
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
