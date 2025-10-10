package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;

public class CarouselRequest {

    @NotBlank(message = "图片地址不能为空")
    private String imageUrl;

    @NotBlank(message = "服务名称不能为空")
    private String serviceName;

    private String linkUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
}
