package com.example.housekeeping.service.impl;

import com.example.housekeeping.domain.entity.RechargeRecord;
import com.example.housekeeping.domain.entity.ServiceProvider;
import com.example.housekeeping.domain.entity.UserAccount;
import com.example.housekeeping.domain.entity.WithdrawalRecord;
import com.example.housekeeping.domain.enums.PaymentStatus;
import com.example.housekeeping.domain.enums.WithdrawalStatus;
import com.example.housekeeping.dto.RechargeRequest;
import com.example.housekeeping.dto.WithdrawalProcessRequest;
import com.example.housekeeping.dto.WithdrawalRequest;
import com.example.housekeeping.repository.RechargeRecordRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.UserAccountRepository;
import com.example.housekeeping.repository.WithdrawalRecordRepository;
import com.example.housekeeping.service.FinanceService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class FinanceServiceImpl implements FinanceService {

    private final RechargeRecordRepository rechargeRecordRepository;
    private final WithdrawalRecordRepository withdrawalRecordRepository;
    private final UserAccountRepository userAccountRepository;
    private final ServiceProviderRepository serviceProviderRepository;

    @Override
    public RechargeRecord createRecharge(Long userId, RechargeRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
        RechargeRecord record = new RechargeRecord();
        record.setUser(user);
        record.setAmount(request.amount());
        record.setPaymentMethod(request.paymentMethod());
        record.setReferenceNo(UUID.randomUUID().toString());
        record.setStatus(PaymentStatus.SUCCESS);
        record.setPaidAt(LocalDateTime.now());
        RechargeRecord saved = rechargeRecordRepository.save(record);
        user.setBalance(user.getBalance().add(request.amount()));
        return saved;
    }

    @Override
    public List<RechargeRecord> getRechargesForUser(Long userId) {
        return rechargeRecordRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<RechargeRecord> getAllRecharges() {
        return rechargeRecordRepository.findAll();
    }

    @Override
    public WithdrawalRecord requestWithdrawal(Long providerId, WithdrawalRequest request) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "服务者不存在"));
        if (provider.getBalance().compareTo(request.amount()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "余额不足");
        }
        WithdrawalRecord record = new WithdrawalRecord();
        record.setProvider(provider);
        record.setAmount(request.amount());
        record.setAccountInfo(request.accountInfo());
        record.setStatus(WithdrawalStatus.PENDING);
        record.setRequestedAt(LocalDateTime.now());
        return withdrawalRecordRepository.save(record);
    }

    @Override
    public WithdrawalRecord processWithdrawal(Long withdrawalId, WithdrawalProcessRequest request) {
        WithdrawalRecord record = withdrawalRecordRepository.findById(withdrawalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "提现记录不存在"));
        record.setStatus(request.status());
        record.setRemark(request.remark());
        record.setProcessedBy(request.processedBy());
        record.setProcessedAt(LocalDateTime.now());
        if (request.status() == WithdrawalStatus.PAID) {
            ServiceProvider provider = record.getProvider();
            if (provider.getBalance().compareTo(record.getAmount()) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "服务者余额不足，无法完成打款");
            }
            provider.setBalance(provider.getBalance().subtract(record.getAmount()));
        }
        return record;
    }

    @Override
    public List<WithdrawalRecord> getWithdrawalsForProvider(Long providerId) {
        return withdrawalRecordRepository.findByProviderIdOrderByCreatedAtDesc(providerId);
    }

    @Override
    public List<WithdrawalRecord> getAllWithdrawals() {
        return withdrawalRecordRepository.findAll();
    }
}
