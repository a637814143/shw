package com.example.housekeeping.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 评价创建/更新请求
 */
@Data
public class DashboardReviewItemRequest {

    @NotBlank(message = "评价人不能为空")
    private String author;

    @NotBlank(message = "服务名称不能为空")
    private String serviceName;

    @Min(value = 1, message = "评分必须在1到5之间")
    @Max(value = 5, message = "评分必须在1到5之间")
    private Integer rating;

    @NotBlank(message = "评价内容不能为空")
    private String content;
}
