package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 贴士创建/更新请求
 */
@Data
public class DashboardTipItemRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;
}
