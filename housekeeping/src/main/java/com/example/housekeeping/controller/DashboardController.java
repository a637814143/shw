package com.example.housekeeping.controller;

import com.example.housekeeping.dto.DashboardStatsResponse;
import com.example.housekeeping.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    public DashboardStatsResponse loadStats() {
        return dashboardService.loadDashboardStats();
    }
}
