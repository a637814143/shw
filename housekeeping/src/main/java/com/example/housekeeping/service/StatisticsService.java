package com.example.housekeeping.service;

import com.example.housekeeping.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务类
 */
@Service
public class StatisticsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Autowired
    private HousekeepingServiceRepository housekeepingServiceRepository;
    
    @Autowired
    private ServiceBookingRepository serviceBookingRepository;
    
    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;
    
    @Autowired
    private WithdrawRecordRepository withdrawRecordRepository;
    
    /**
     * 获取基础统计数据
     */
    public Map<String, Object> getBasicStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 用户数量
        long userCount = userRepository.countActiveUsers();
        statistics.put("userCount", userCount);
        
        // 服务者数量
        long providerCount = providerRepository.countActiveProviders();
        statistics.put("providerCount", providerCount);
        
        // 服务数量
        long serviceCount = housekeepingServiceRepository.countActiveServices();
        statistics.put("serviceCount", serviceCount);
        
        // 预约数量
        long bookingCount = serviceBookingRepository.countActiveBookings();
        statistics.put("bookingCount", bookingCount);
        
        // 待分配预约数量
        long pendingBookingCount = serviceBookingRepository.countPendingBookings();
        statistics.put("pendingBookingCount", pendingBookingCount);
        
        // 待审核服务者数量
        long pendingProviderCount = providerRepository.countPendingProviders();
        statistics.put("pendingProviderCount", pendingProviderCount);
        
        // 待审核提现数量
        long pendingWithdrawCount = withdrawRecordRepository.countPendingWithdraws();
        statistics.put("pendingWithdrawCount", pendingWithdrawCount);
        
        // 充值总金额
        BigDecimal totalRechargeAmount = rechargeRecordRepository.sumTotalRechargeAmount();
        statistics.put("totalRechargeAmount", totalRechargeAmount);
        
        // 提现总金额
        BigDecimal totalWithdrawAmount = withdrawRecordRepository.sumTotalWithdrawAmount();
        statistics.put("totalWithdrawAmount", totalWithdrawAmount);
        
        return statistics;
    }
    
    /**
     * 获取一周充值趋势数据
     */
    public List<Map<String, Object>> getWeeklyRechargeTrend() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(7);
        
        List<Object[]> results = rechargeRecordRepository.getWeeklyRechargeTrend(startTime);
        List<Map<String, Object>> trendData = new ArrayList<>();
        
        for (Object[] result : results) {
            Map<String, Object> data = new HashMap<>();
            data.put("date", result[0]);
            data.put("amount", result[1]);
            trendData.add(data);
        }
        
        return trendData;
    }
    
    /**
     * 获取服务预约量对比数据
     */
    public List<Map<String, Object>> getServiceBookingComparison() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(7);
        
        List<Map<String, Object>> comparisonData = new ArrayList<>();
        
        // 本周预约量
        long thisWeekBookings = serviceBookingRepository.countBookingsByTimeRange(startTime, endTime);
        
        // 上周预约量
        LocalDateTime lastWeekStart = startTime.minusDays(7);
        LocalDateTime lastWeekEnd = startTime;
        long lastWeekBookings = serviceBookingRepository.countBookingsByTimeRange(lastWeekStart, lastWeekEnd);
        
        Map<String, Object> thisWeek = new HashMap<>();
        thisWeek.put("period", "本周");
        thisWeek.put("count", thisWeekBookings);
        comparisonData.add(thisWeek);
        
        Map<String, Object> lastWeek = new HashMap<>();
        lastWeek.put("period", "上周");
        lastWeek.put("count", lastWeekBookings);
        comparisonData.add(lastWeek);
        
        return comparisonData;
    }
}
