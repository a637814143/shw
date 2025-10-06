package com.example.housekeeping.service.impl;

import com.example.housekeeping.domain.entity.HousekeepingService;
import com.example.housekeeping.domain.entity.RechargeRecord;
import com.example.housekeeping.domain.entity.ServiceAppointment;
import com.example.housekeeping.domain.entity.WithdrawalRecord;
import com.example.housekeeping.domain.enums.AppointmentStatus;
import com.example.housekeeping.domain.enums.CertificationStatus;
import com.example.housekeeping.dto.DashboardStatsResponse;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.ProviderCertificationRepository;
import com.example.housekeeping.repository.RechargeRecordRepository;
import com.example.housekeeping.repository.ServiceAppointmentRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserAccountRepository;
import com.example.housekeeping.repository.WithdrawalRecordRepository;
import com.example.housekeeping.service.DashboardService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardServiceImpl implements DashboardService {

    private final UserAccountRepository userAccountRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final HousekeepingServiceRepository housekeepingServiceRepository;
    private final ServiceAppointmentRepository serviceAppointmentRepository;
    private final RechargeRecordRepository rechargeRecordRepository;
    private final WithdrawalRecordRepository withdrawalRecordRepository;
    private final ProviderCertificationRepository providerCertificationRepository;

    @Override
    public DashboardStatsResponse loadDashboardStats() {
        long totalUsers = userAccountRepository.count();
        long totalProviders = serviceProviderRepository.count();
        long totalServices = housekeepingServiceRepository.count();
        long totalAppointments = serviceAppointmentRepository.count();
        long pendingAppointments = serviceAppointmentRepository.countByStatus(AppointmentStatus.PENDING_REVIEW);
        long pendingCertifications = providerCertificationRepository.countByStatus(CertificationStatus.PENDING);
        long totalRecharges = rechargeRecordRepository.count();
        long totalWithdrawals = withdrawalRecordRepository.count();

        BigDecimal rechargeSum = rechargeRecordRepository.findAll().stream()
                .map(recharge -> recharge.getAmount() != null ? recharge.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal withdrawalSum = withdrawalRecordRepository.findAll().stream()
                .map(withdrawal -> withdrawal.getAmount() != null ? withdrawal.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DashboardStatsResponse.TrendPoint> rechargeTrend = buildRechargeTrend();
        List<DashboardStatsResponse.TrendPoint> appointmentTrend = buildAppointmentTrend();
        List<DashboardStatsResponse.CategoryCount> categoryDistribution = buildCategoryDistribution();

        return new DashboardStatsResponse(
                totalUsers,
                totalProviders,
                totalServices,
                totalAppointments,
                pendingAppointments,
                pendingCertifications,
                totalRecharges,
                totalWithdrawals,
                rechargeSum.setScale(2, RoundingMode.HALF_UP),
                withdrawalSum.setScale(2, RoundingMode.HALF_UP),
                rechargeTrend,
                appointmentTrend,
                categoryDistribution
        );
    }

    private List<DashboardStatsResponse.TrendPoint> buildRechargeTrend() {
        List<RechargeRecord> records = rechargeRecordRepository.findAll();
        LocalDate today = LocalDate.now();
        List<DashboardStatsResponse.TrendPoint> trendPoints = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            BigDecimal sum = records.stream()
                    .filter(record -> record.getCreatedAt() != null && record.getCreatedAt().toLocalDate().equals(day))
                    .map(record -> record.getAmount() != null ? record.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            trendPoints.add(new DashboardStatsResponse.TrendPoint(day.toString(), sum.setScale(2, RoundingMode.HALF_UP)));
        }
        return trendPoints;
    }

    private List<DashboardStatsResponse.TrendPoint> buildAppointmentTrend() {
        List<ServiceAppointment> appointments = serviceAppointmentRepository.findAll();
        LocalDate today = LocalDate.now();
        List<DashboardStatsResponse.TrendPoint> trendPoints = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            long count = appointments.stream()
                    .filter(appointment -> appointment.getCreatedAt() != null
                            && appointment.getCreatedAt().toLocalDate().equals(day))
                    .count();
            trendPoints.add(new DashboardStatsResponse.TrendPoint(day.toString(), count));
        }
        return trendPoints;
    }

    private List<DashboardStatsResponse.CategoryCount> buildCategoryDistribution() {
        List<HousekeepingService> services = housekeepingServiceRepository.findAll();
        Map<String, Long> grouped = services.stream()
                .collect(Collectors.groupingBy(service -> service.getCategory() != null
                                ? service.getCategory().getName()
                                : "未分类",
                        Collectors.counting()));
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new DashboardStatsResponse.CategoryCount(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
