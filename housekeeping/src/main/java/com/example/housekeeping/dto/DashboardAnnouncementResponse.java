package com.example.housekeeping.dto;

/**
 * 系统公告响应。
 */
public class DashboardAnnouncementResponse {

    private final Long id;
    private final String title;
    private final String content;

    public DashboardAnnouncementResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
