package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 系统公告请求。
 */
public class DashboardAnnouncementRequest {

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "公告标题过长")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Size(max = 2000, message = "公告内容过长")
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
