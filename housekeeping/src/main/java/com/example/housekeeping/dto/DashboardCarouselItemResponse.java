package com.example.housekeeping.dto;

/**
 * 轮播图数据响应。
 */
public class DashboardCarouselItemResponse {

    private final Long id;
    private final String title;
    private final String imageUrl;
    private final String serviceLink;

    public DashboardCarouselItemResponse(Long id, String title, String imageUrl, String serviceLink) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.serviceLink = serviceLink;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getServiceLink() {
        return serviceLink;
    }
}
