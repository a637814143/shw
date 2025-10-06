package com.example.housekeeping.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardStatsResponse(
        long totalUsers,
        long totalProviders,
        long totalServices,
        long totalAppointments,
        long pendingAppointments,
        long pendingCertifications,
        long totalRecharges,
        long totalWithdrawals,
        BigDecimal rechargeSum,
        BigDecimal withdrawalSum,
        List<TrendPoint> rechargeTrend,
        List<TrendPoint> appointmentTrend,
        List<CategoryCount> serviceCategoryDistribution
) {
    public record TrendPoint(String label, Number value) {}

    public record CategoryCount(String label, Number value) {}
}
