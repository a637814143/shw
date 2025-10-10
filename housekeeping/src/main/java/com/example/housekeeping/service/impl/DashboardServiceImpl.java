package com.example.housekeeping.service.impl;

import com.example.housekeeping.dto.response.BookingTrendPoint;
import com.example.housekeeping.dto.response.DashboardSummaryResponse;
import com.example.housekeeping.enums.BookingStatus;
import com.example.housekeeping.enums.CertificationStatus;
import com.example.housekeeping.enums.WithdrawStatus;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.ProviderCertificationRepository;
import com.example.housekeeping.repository.RechargeRecordRepository;
import com.example.housekeeping.repository.ServiceBookingRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserRepository;
import com.example.housekeeping.repository.WithdrawRecordRepository;
import com.example.housekeeping.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final HousekeepingServiceRepository housekeepingServiceRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final RechargeRecordRepository rechargeRecordRepository;
    private final WithdrawRecordRepository withdrawRecordRepository;
    private final ProviderCertificationRepository providerCertificationRepository;

    @Override
    public DashboardSummaryResponse getSummary() {
        BigDecimal totalRecharge = rechargeRecordRepository.sumAmountByStatus(null);
        BigDecimal totalWithdraw = withdrawRecordRepository.sumAmountByStatus(null);
        if (totalRecharge == null) {
            totalRecharge = BigDecimal.ZERO;
        }
        if (totalWithdraw == null) {
            totalWithdraw = BigDecimal.ZERO;
        }
        return DashboardSummaryResponse.builder()
                .userCount(userRepository.count())
                .providerCount(serviceProviderRepository.count())
                .serviceCount(housekeepingServiceRepository.count())
                .bookingCount(serviceBookingRepository.count())
                .totalRecharge(totalRecharge)
                .totalWithdraw(totalWithdraw)
                .pendingCertificationCount(providerCertificationRepository.countByStatus(CertificationStatus.PENDING))
                .pendingWithdrawCount(withdrawRecordRepository.countByStatus(WithdrawStatus.PENDING))
                .pendingBookingCount(serviceBookingRepository.countByStatus(BookingStatus.PENDING))
                .build();
    }

    @Override
    public List<BookingTrendPoint> getBookingTrend(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        List<Object[]> rows = serviceBookingRepository.countBookingsByDay(start, end);
        List<BookingTrendPoint> result = new ArrayList<>();
        for (Object[] row : rows) {
            Object dateValue = row[0];
            LocalDate date;
            if (dateValue instanceof LocalDate localDate) {
                date = localDate;
            } else if (dateValue instanceof java.sql.Date sqlDate) {
                date = sqlDate.toLocalDate();
            } else {
                throw new IllegalStateException("无法解析预约日期");
            }
            Number value = (Number) row[1];
            result.add(new BookingTrendPoint(date, value.longValue()));
        }
        return result;
    }
}
