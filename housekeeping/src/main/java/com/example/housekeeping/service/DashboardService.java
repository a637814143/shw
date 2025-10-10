package com.example.housekeeping.service;

import com.example.housekeeping.dto.response.BookingTrendPoint;
import com.example.housekeeping.dto.response.DashboardSummaryResponse;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

    DashboardSummaryResponse getSummary();

    List<BookingTrendPoint> getBookingTrend(LocalDate startDate, LocalDate endDate);
}
