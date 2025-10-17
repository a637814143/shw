package com.example.housekeeping.service;

import com.example.housekeeping.dto.AccountTransactionResponse;
import com.example.housekeeping.dto.AdminOverviewResponse;
import com.example.housekeeping.dto.ServiceFavoriteResponse;
import com.example.housekeeping.entity.AccountTransaction;
import com.example.housekeeping.entity.ServiceFavorite;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.enums.AccountTransactionType;
import com.example.housekeeping.enums.ServiceOrderStatus;
import com.example.housekeeping.repository.AccountTransactionRepository;
import com.example.housekeeping.repository.ServiceFavoriteRepository;
import com.example.housekeeping.repository.ServiceOrderRepository;
import com.example.housekeeping.repository.UserAllRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员仪表盘统计。
 */
@Service
public class AdminInsightService {

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private AccountTransactionRepository accountTransactionRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private UserAllRepository userAllRepository;

    @Autowired
    private ServiceFavoriteRepository serviceFavoriteRepository;

    @Transactional(readOnly = true)
    public AdminOverviewResponse loadOverview() {
        UserAll admin = ensureAdmin();

        List<AccountTransaction> allTransactions = accountTransactionRepository.findAll();
        BigDecimal totalRecharge = sumByType(allTransactions, AccountTransactionType.RECHARGE);
        BigDecimal totalWithdraw = sumByType(allTransactions, AccountTransactionType.WITHDRAW);

        long totalUsers = userAllRepository.countByUserType(AccountRole.USER.getLabel());
        long totalCompanies = userAllRepository.countByUserType(AccountRole.COMPANY.getLabel());
        long totalAdmins = userAllRepository.countByUserType(AccountRole.ADMIN.getLabel());

        List<AdminOverviewResponse.TrendPoint> weeklyTrend = buildWeeklyTrend(allTransactions);
        List<AdminOverviewResponse.AppointmentMetric> appointmentMetrics = buildAppointmentMetrics();

        return new AdminOverviewResponse(
            totalRecharge.setScale(2, RoundingMode.HALF_UP),
            totalWithdraw.setScale(2, RoundingMode.HALF_UP),
            totalUsers,
            totalCompanies,
            totalAdmins,
            weeklyTrend,
            appointmentMetrics
        );
    }

    @Transactional(readOnly = true)
    public List<AccountTransactionResponse> listRecentTransactions() {
        ensureAdmin();
        return accountTransactionRepository.findTop50ByOrderByCreatedAtDesc().stream()
            .map(txn -> new AccountTransactionResponse(
                txn.getId(),
                txn.getUser().getUsername(),
                txn.getType(),
                txn.getAmount(),
                txn.getNote(),
                txn.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceFavoriteResponse> listFavorites() {
        ensureAdmin();
        return serviceFavoriteRepository.findAll().stream()
            .sorted(Comparator.comparing(ServiceFavorite::getCreatedAt).reversed())
            .map(favorite -> new ServiceFavoriteResponse(
                favorite.getId(),
                favorite.getUser().getUsername(),
                favorite.getService().getId(),
                favorite.getService().getName(),
                favorite.getService().getCompany().getUsername(),
                favorite.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    private UserAll ensureAdmin() {
        UserAll admin = accountLookupService.getCurrentAccount();
        if (!AccountRole.ADMIN.getLabel().equals(admin.getUserType())) {
            throw new RuntimeException("仅管理员可访问");
        }
        return admin;
    }

    private BigDecimal sumByType(List<AccountTransaction> transactions, AccountTransactionType type) {
        return transactions.stream()
            .filter(txn -> txn.getType() == type)
            .map(AccountTransaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<AdminOverviewResponse.TrendPoint> buildWeeklyTrend(List<AccountTransaction> transactions) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);
        Map<LocalDate, BigDecimal> daily = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(6 - i);
            daily.put(date, BigDecimal.ZERO);
        }

        transactions.stream()
            .filter(txn -> txn.getType() == AccountTransactionType.RECHARGE)
            .forEach(txn -> {
                LocalDate date = LocalDate.ofInstant(txn.getCreatedAt(), zone);
                if (daily.containsKey(date)) {
                    daily.put(date, daily.get(date).add(txn.getAmount()));
                }
            });

        List<AdminOverviewResponse.TrendPoint> points = new ArrayList<>();
        daily.forEach((date, amount) -> points.add(new AdminOverviewResponse.TrendPoint(date.toString(), amount.setScale(2, RoundingMode.HALF_UP))));
        return points;
    }

    private List<AdminOverviewResponse.AppointmentMetric> buildAppointmentMetrics() {
        Map<ServiceOrderStatus, Long> counts = new EnumMap<>(ServiceOrderStatus.class);
        serviceOrderRepository.findAll().forEach(order -> counts.merge(order.getStatus(), 1L, Long::sum));

        List<AdminOverviewResponse.AppointmentMetric> metrics = new ArrayList<>();
        metrics.add(new AdminOverviewResponse.AppointmentMetric("待上门", counts.getOrDefault(ServiceOrderStatus.SCHEDULED, 0L)));
        metrics.add(new AdminOverviewResponse.AppointmentMetric("服务中", counts.getOrDefault(ServiceOrderStatus.IN_PROGRESS, 0L)));
        metrics.add(new AdminOverviewResponse.AppointmentMetric("已完成", counts.getOrDefault(ServiceOrderStatus.COMPLETED, 0L)));
        metrics.add(new AdminOverviewResponse.AppointmentMetric("退款处理", counts.getOrDefault(ServiceOrderStatus.REFUND_REQUESTED, 0L)));
        return metrics;
    }
}
