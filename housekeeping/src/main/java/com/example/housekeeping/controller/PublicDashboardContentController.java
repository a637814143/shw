package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.DashboardAnnouncementResponse;
import com.example.housekeeping.dto.DashboardTipItemResponse;
import com.example.housekeeping.service.DashboardContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对游客开放的仪表盘内容接口。
 */
@RestController
@RequestMapping("/api/public/dashboard")
public class PublicDashboardContentController {

    @Autowired
    private DashboardContentService dashboardContentService;

    @GetMapping("/tips")
    public Result<List<DashboardTipItemResponse>> listTips(
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return Result.success(dashboardContentService.listTips(keyword));
    }

    @GetMapping("/announcements")
    public Result<List<DashboardAnnouncementResponse>> listAnnouncements(
        @RequestParam(value = "keyword", required = false) String keyword
    ) {
        return Result.success(dashboardContentService.listAnnouncements(keyword));
    }
}
