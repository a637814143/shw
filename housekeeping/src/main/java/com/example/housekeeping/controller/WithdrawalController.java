package com.example.housekeeping.controller;

import com.example.housekeeping.dto.WithdrawalRequest;
import com.example.housekeeping.entity.WithdrawalRecord;
import com.example.housekeeping.exception.ResourceNotFoundException;
import com.example.housekeeping.repository.WithdrawalRecordRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalRecordRepository withdrawalRepository;

    public WithdrawalController(WithdrawalRecordRepository withdrawalRepository) {
        this.withdrawalRepository = withdrawalRepository;
    }

    @GetMapping
    public Page<WithdrawalRecord> list(@RequestParam(required = false) String providerName,
                                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by("withdrawalTime").descending());
        String normalizedName = providerName != null && !providerName.isBlank() ? providerName : null;
        return withdrawalRepository.search(normalizedName, date, pageable);
    }

    @GetMapping("/{id}")
    public WithdrawalRecord detail(@PathVariable Long id) {
        return withdrawalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的提现记录"));
    }

    @PostMapping
    public ResponseEntity<WithdrawalRecord> create(@Valid @RequestBody WithdrawalRequest request) {
        WithdrawalRecord record = WithdrawalRecord.builder()
                .amount(request.getAmount())
                .accountType(request.getAccountType())
                .accountNumber(request.getAccountNumber())
                .providerName(request.getProviderName())
                .withdrawalTime(request.getWithdrawalTime())
                .build();
        WithdrawalRecord saved = withdrawalRepository.save(record);
        return ResponseEntity.created(URI.create("/api/withdrawals/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public WithdrawalRecord update(@PathVariable Long id, @Valid @RequestBody WithdrawalRequest request) {
        WithdrawalRecord record = withdrawalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的提现记录"));
        record.setAmount(request.getAmount());
        record.setAccountType(request.getAccountType());
        record.setAccountNumber(request.getAccountNumber());
        record.setProviderName(request.getProviderName());
        record.setWithdrawalTime(request.getWithdrawalTime());
        return withdrawalRepository.save(record);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        WithdrawalRecord record = withdrawalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到对应的提现记录"));
        withdrawalRepository.delete(record);
        return ResponseEntity.noContent().build();
    }
}
