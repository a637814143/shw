package com.example.housekeeping.service;

import com.example.housekeeping.common.PageResult;
import com.example.housekeeping.entity.*;
import com.example.housekeeping.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 家政人员业务服务类
 */
@Service
public class ProviderService {
    
    @Autowired
    private ProviderRepository providerRepository;
    
    @Autowired
    private HousekeepingServiceRepository housekeepingServiceRepository;
    
    @Autowired
    private ServiceBookingRepository serviceBookingRepository;
    
    @Autowired
    private WithdrawRecordRepository withdrawRecordRepository;
    
    @Autowired
    private ServiceReviewRepository serviceReviewRepository;
    
    @Autowired
    private SystemNoticeRepository systemNoticeRepository;
    
    /**
     * 获取家政人员自己的服务列表
     */
    public PageResult<HousekeepingService> getMyServices(String username, int page, int size) {
        Provider provider = providerRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new RuntimeException("家政人员不存在"));
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<HousekeepingService> servicePage = housekeepingServiceRepository.findByConditions(
                null, null, provider.getId(), 1, pageable);
        
        return PageResult.of(servicePage);
    }
    
    /**
     * 获取分配给家政人员的预约列表
     */
    public PageResult<ServiceBooking> getMyBookings(String username, int page, int size, Integer status) {
        Provider provider = providerRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new RuntimeException("家政人员不存在"));
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ServiceBooking> bookingPage = serviceBookingRepository.findByConditions(
                null, null, provider.getId(), status, pageable);
        
        return PageResult.of(bookingPage);
    }
    
    /**
     * 更新预约状态
     */
    public void updateBookingStatus(String username, Long bookingId, Integer status) {
        Provider provider = providerRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new RuntimeException("家政人员不存在"));
        
        ServiceBooking booking = serviceBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        
        // 检查预约是否分配给当前家政人员
        if (!provider.getId().equals(booking.getProviderId())) {
            throw new RuntimeException("无权操作此预约");
        }
        
        booking.setStatus(status);
        serviceBookingRepository.save(booking);
    }
    
    /**
     * 获取家政人员的提现记录
     */
    public PageResult<WithdrawRecord> getMyWithdrawRecords(String username, int page, int size, Integer status) {
        Provider provider = providerRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new RuntimeException("家政人员不存在"));
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<WithdrawRecord> withdrawPage = withdrawRecordRepository.findByConditions(
                provider.getId(), status, pageable);
        
        return PageResult.of(withdrawPage);
    }
    
    /**
     * 申请提现
     */
    public void applyWithdraw(String username, BigDecimal amount, String bankName, 
                             String bankAccount, String accountHolder) {
        Provider provider = providerRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new RuntimeException("家政人员不存在"));
        
        // 检查余额是否足够
        if (provider.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        // 创建提现记录
        WithdrawRecord withdrawRecord = new WithdrawRecord();
        withdrawRecord.setProviderId(provider.getId());
        withdrawRecord.setAmount(amount);
        withdrawRecord.setBankName(bankName);
        withdrawRecord.setBankAccount(bankAccount);
        withdrawRecord.setAccountHolder(accountHolder);
        withdrawRecord.setStatus(0); // 待审核
        
        withdrawRecordRepository.save(withdrawRecord);
        
        // 扣除余额
        provider.setBalance(provider.getBalance().subtract(amount));
        providerRepository.save(provider);
    }
    
    /**
     * 获取家政人员的服务评价
     */
    public PageResult<ServiceReview> getMyReviews(String username, int page, int size) {
        Provider provider = providerRepository.findByUsernameAndStatus(username, 1)
                .orElseThrow(() -> new RuntimeException("家政人员不存在"));
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<ServiceReview> reviewPage = serviceReviewRepository.findByConditions(
                null, provider.getId(), null, pageable);
        
        return PageResult.of(reviewPage);
    }
    
    /**
     * 获取系统公告
     */
    public List<SystemNotice> getSystemNotices() {
        return systemNoticeRepository.findByStatusOrderByCreateTimeDesc(1);
    }
    
    // ========== 管理员相关方法 ==========
    
    /**
     * 分页查询服务者（管理员）
     */
    public PageResult<Provider> getProviders(int page, int size, String username, String realName, String phone, Integer status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Provider> providerPage = providerRepository.findByConditions(username, realName, phone, status, pageable);
        return PageResult.of(providerPage);
    }
    
    /**
     * 根据ID获取服务者
     */
    public Provider getProviderById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("服务者不存在"));
    }
    
    /**
     * 创建服务者
     */
    public Provider createProvider(Provider provider) {
        // 检查用户名是否已存在
        if (providerRepository.existsByUsername(provider.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (providerRepository.existsByPhone(provider.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 设置默认状态为待审核
        provider.setStatus(0);
        
        return providerRepository.save(provider);
    }
    
    /**
     * 更新服务者
     */
    public Provider updateProvider(Long id, Provider provider) {
        Provider existingProvider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("服务者不存在"));
        
        // 检查用户名是否被其他用户使用
        if (!existingProvider.getUsername().equals(provider.getUsername()) && 
            providerRepository.existsByUsername(provider.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否被其他用户使用
        if (!existingProvider.getPhone().equals(provider.getPhone()) && 
            providerRepository.existsByPhone(provider.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }
        
        // 更新字段
        existingProvider.setUsername(provider.getUsername());
        existingProvider.setRealName(provider.getRealName());
        existingProvider.setPhone(provider.getPhone());
        existingProvider.setEmail(provider.getEmail());
        existingProvider.setAvatar(provider.getAvatar());
        existingProvider.setIdCard(provider.getIdCard());
        existingProvider.setIdCardFront(provider.getIdCardFront());
        existingProvider.setIdCardBack(provider.getIdCardBack());
        existingProvider.setCertificate(provider.getCertificate());
        existingProvider.setExperience(provider.getExperience());
        
        return providerRepository.save(existingProvider);
    }
    
    /**
     * 删除服务者
     */
    public void deleteProvider(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("服务者不存在"));
        
        // 检查是否有未完成的预约
        long activeBookings = serviceBookingRepository.countByProviderIdAndStatusIn(id, List.of(0, 1, 2));
        if (activeBookings > 0) {
            throw new RuntimeException("该服务者还有未完成的预约，无法删除");
        }
        
        providerRepository.delete(provider);
    }
    
    /**
     * 审核服务者
     */
    public void auditProvider(Long id, Integer status, String remark) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("服务者不存在"));
        
        provider.setStatus(status);
        providerRepository.save(provider);
        
        // 可以在这里添加审核记录逻辑
    }
    
    /**
     * 重置服务者密码
     */
    public void resetProviderPassword(Long id, String newPassword) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("服务者不存在"));
        
        provider.setPassword(newPassword);
        providerRepository.save(provider);
    }
}