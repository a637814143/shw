package com.example.housekeeping.service;

import com.example.housekeeping.domain.entity.RechargeRecord;
import com.example.housekeeping.domain.entity.WithdrawalRecord;
import com.example.housekeeping.dto.RechargeRequest;
import com.example.housekeeping.dto.WithdrawalProcessRequest;
import com.example.housekeeping.dto.WithdrawalRequest;
import java.util.List;

public interface FinanceService {
    RechargeRecord createRecharge(Long userId, RechargeRequest request);

    List<RechargeRecord> getRechargesForUser(Long userId);

    List<RechargeRecord> getAllRecharges();

    WithdrawalRecord requestWithdrawal(Long providerId, WithdrawalRequest request);

    WithdrawalRecord processWithdrawal(Long withdrawalId, WithdrawalProcessRequest request);

    List<WithdrawalRecord> getWithdrawalsForProvider(Long providerId);

    List<WithdrawalRecord> getAllWithdrawals();
}
