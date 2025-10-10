package com.example.housekeeping.controller.admin;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.entity.RechargeRecord;
import com.example.housekeeping.enums.RechargeStatus;
import com.example.housekeeping.repository.RechargeRecordRepository;
import com.example.housekeeping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/recharges")
@RequiredArgsConstructor
public class AdminRechargeController {

    private final RechargeRecordRepository rechargeRecordRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<Page<RechargeRecord>> list(@RequestParam(required = false) RechargeStatus status, Pageable pageable) {
        Page<RechargeRecord> page = status == null
                ? rechargeRecordRepository.findAll(pageable)
                : rechargeRecordRepository.findByStatus(status, pageable);
        return ApiResponse.success(page);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<RechargeRecord> updateStatus(@PathVariable Long id, @RequestParam RechargeStatus status) {
        RechargeRecord record = rechargeRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("充值记录不存在"));
        RechargeStatus previous = record.getStatus();
        record.setStatus(status);
        rechargeRecordRepository.save(record);
        if (status == RechargeStatus.SUCCESS && previous != RechargeStatus.SUCCESS) {
            if (record.getUser().getBalance() == null) {
                record.getUser().setBalance(record.getAmount());
            } else {
                record.getUser().setBalance(record.getUser().getBalance().add(record.getAmount()));
            }
            userRepository.save(record.getUser());
        }
        return ApiResponse.success(record);
    }
}
