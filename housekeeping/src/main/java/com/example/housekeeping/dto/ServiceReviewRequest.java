package com.example.housekeeping.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 普通用户提交评价的请求。
 */
public class ServiceReviewRequest {

    @NotNull(message = "请选择服务")
    private Long serviceId;

    @Min(value = 1, message = "评分需在1-5之间")
    @Max(value = 5, message = "评分需在1-5之间")
    private Integer rating;

    @NotBlank(message = "请填写评价内容")
    @Size(max = 500, message = "评价内容过长")
    private String content;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
