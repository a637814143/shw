package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 轮播图管理请求。
 */
public class DashboardCarouselItemRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题过长")
    private String title;

    @NotBlank(message = "图片地址不能为空")
    @Size(max = 500, message = "图片地址过长")
    private String imageUrl;

    @Size(max = 200, message = "跳转信息过长")
    private String serviceLink;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServiceLink() {
        return serviceLink;
    }

    public void setServiceLink(String serviceLink) {
        this.serviceLink = serviceLink;
    }
}
