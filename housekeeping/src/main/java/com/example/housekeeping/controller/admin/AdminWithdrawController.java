package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.WithdrawProcessRequest;
import com.example.housekeeping.entity.WithdrawRecord;
import com.example.housekeeping.enums.WithdrawStatus;
import com.example.housekeeping.repository.WithdrawRecordRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/withdraws")
@RequiredArgsConstructor
public class AdminWithdrawController {

    private final WithdrawRecordRepository withdrawRecordRepository;
    private final ServiceProviderRepository providerRepository;

    @GetMapping
    public ApiResponse<Page<WithdrawRecord>> list(@RequestParam(required = false) WithdrawStatus status, Pageable pageable) {
        Page<WithdrawRecord> page = status == null
                ? withdrawRecordRepository.findAll(pageable)
                : withdrawRecordRepository.findByStatus(status, pageable);
        return ApiResponse.success(page);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<WithdrawRecord> updateStatus(@PathVariable Long id,
                                                     @RequestBody @Valid WithdrawProcessRequest request) {
        WithdrawRecord record = withdrawRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("提现记录不存在"));
        WithdrawStatus previous = record.getStatus();
        record.setStatus(request.getStatus());
        record.setRemark(request.getRemark());
        withdrawRecordRepository.save(record);
        if ((request.getStatus() == WithdrawStatus.APPROVED || request.getStatus() == WithdrawStatus.PAID)
                && previous != WithdrawStatus.APPROVED && previous != WithdrawStatus.PAID) {
            if (record.getProvider().getBalance() != null) {
                record.getProvider().setBalance(record.getProvider().getBalance().subtract(record.getAmount()));
            }
            providerRepository.save(record.getProvider());
        } else if (request.getStatus() == WithdrawStatus.REJECTED
                && (previous == WithdrawStatus.APPROVED || previous == WithdrawStatus.PAID)) {
            if (record.getProvider().getBalance() != null) {
                record.getProvider().setBalance(record.getProvider().getBalance().add(record.getAmount()));
            }
            providerRepository.save(record.getProvider());
        }
        return ApiResponse.success(record);
    }
}
