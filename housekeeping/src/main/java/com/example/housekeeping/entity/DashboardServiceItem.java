package com.example.housekeeping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户端展示的服务条目
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "dashboard_service_item")
public class DashboardServiceItem extends BaseEntity {

    @NotBlank(message = "服务名称不能为空")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "服务描述不能为空")
    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @NotBlank(message = "服务图标不能为空")
    @Column(name = "icon", nullable = false, length = 20)
    private String icon;
}
