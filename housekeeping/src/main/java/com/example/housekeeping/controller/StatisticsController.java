package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/api/admin/statistics")
public class StatisticsController {
    
    @Autowired
    private StatisticsService statisticsService;
    
    /**
     * 获取基础统计数据
     */
    @GetMapping("/basic")
    public Result<Map<String, Object>> getBasicStatistics() {
        try {
            Map<String, Object> statistics = statisticsService.getBasicStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取一周充值趋势数据
     */
    @GetMapping("/recharge-trend")
    public Result<List<Map<String, Object>>> getWeeklyRechargeTrend() {
        try {
            List<Map<String, Object>> trendData = statisticsService.getWeeklyRechargeTrend();
            return Result.success(trendData);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取服务预约量对比数据
     */
    @GetMapping("/booking-comparison")
    public Result<List<Map<String, Object>>> getServiceBookingComparison() {
        try {
            List<Map<String, Object>> comparisonData = statisticsService.getServiceBookingComparison();
            return Result.success(comparisonData);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
