package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 服务条目创建/更新请求
 */
@Data
public class DashboardServiceItemRequest {

    @NotBlank(message = "服务名称不能为空")
    private String name;

    @NotBlank(message = "服务描述不能为空")
    private String description;

    @NotBlank(message = "图标不能为空")
    private String icon;
}
