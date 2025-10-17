package com.example.housekeeping.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 管理员仪表盘概览数据。
 */
public class AdminOverviewResponse {

    private final BigDecimal totalRecharge;
    private final BigDecimal totalWithdraw;
    private final long totalUsers;
    private final long totalCompanies;
    private final long totalAdmins;
    private final List<TrendPoint> weeklyRecharge;
    private final List<AppointmentMetric> appointmentMetrics;

    public AdminOverviewResponse(BigDecimal totalRecharge, BigDecimal totalWithdraw,
                                 long totalUsers, long totalCompanies, long totalAdmins,
                                 List<TrendPoint> weeklyRecharge, List<AppointmentMetric> appointmentMetrics) {
        this.totalRecharge = totalRecharge;
        this.totalWithdraw = totalWithdraw;
        this.totalUsers = totalUsers;
        this.totalCompanies = totalCompanies;
        this.totalAdmins = totalAdmins;
        this.weeklyRecharge = weeklyRecharge;
        this.appointmentMetrics = appointmentMetrics;
    }

    public BigDecimal getTotalRecharge() {
        return totalRecharge;
    }

    public BigDecimal getTotalWithdraw() {
        return totalWithdraw;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalCompanies() {
        return totalCompanies;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public List<TrendPoint> getWeeklyRecharge() {
        return weeklyRecharge;
    }

    public List<AppointmentMetric> getAppointmentMetrics() {
        return appointmentMetrics;
    }

    public static class TrendPoint {
        private final String label;
        private final BigDecimal amount;

        public TrendPoint(String label, BigDecimal amount) {
            this.label = label;
            this.amount = amount;
        }

        public String getLabel() {
            return label;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    public static class AppointmentMetric {
        private final String status;
        private final long count;

        public AppointmentMetric(String status, long count) {
            this.status = status;
            this.count = count;
        }

        public String getStatus() {
            return status;
        }

        public long getCount() {
            return count;
        }
    }
}
