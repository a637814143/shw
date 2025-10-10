package com.example.housekeeping.controller.provider;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.WithdrawCreateRequest;
import com.example.housekeeping.entity.ServiceProvider;
import com.example.housekeeping.entity.WithdrawRecord;
import com.example.housekeeping.enums.WithdrawStatus;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.WithdrawRecordRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/provider/withdraws")
@RequiredArgsConstructor
public class ProviderWithdrawController {

    private final WithdrawRecordRepository withdrawRecordRepository;
    private final ServiceProviderRepository providerRepository;

    @GetMapping
    public ApiResponse<Page<WithdrawRecord>> list(@RequestParam Long providerId, Pageable pageable) {
        ServiceProvider provider = providerRepository.findById(providerId)
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        return ApiResponse.success(withdrawRecordRepository.findByProvider(provider, pageable));
    }

    @PostMapping
    public ApiResponse<WithdrawRecord> create(@RequestBody @Valid WithdrawCreateRequest request) {
        ServiceProvider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new BusinessException("服务者不存在"));
        if (provider.getBalance() != null && provider.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("账户余额不足");
        }
        WithdrawRecord record = new WithdrawRecord();
        record.setProvider(provider);
        record.setAmount(request.getAmount());
        record.setBankName(request.getBankName());
        record.setBankAccount(request.getBankAccount());
        record.setAccountHolder(request.getAccountHolder());
        record.setStatus(WithdrawStatus.PENDING);
        return ApiResponse.success(withdrawRecordRepository.save(record));
    }
}
