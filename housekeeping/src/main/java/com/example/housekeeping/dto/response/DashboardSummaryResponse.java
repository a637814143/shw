package com.example.housekeeping.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardSummaryResponse {

    private long userCount;
    private long providerCount;
    private long serviceCount;
    private long bookingCount;
    private BigDecimal totalRecharge;
    private BigDecimal totalWithdraw;
    private long pendingCertificationCount;
    private long pendingWithdrawCount;
    private long pendingBookingCount;
}
