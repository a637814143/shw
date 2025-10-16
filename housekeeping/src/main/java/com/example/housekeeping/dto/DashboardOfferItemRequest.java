package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 特惠创建/更新请求
 */
@Data
public class DashboardOfferItemRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String tag;
}
