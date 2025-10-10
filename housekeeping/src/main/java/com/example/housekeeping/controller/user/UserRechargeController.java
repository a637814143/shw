package com.example.housekeeping.controller.user;

import com.example.housekeeping.common.ApiResponse;
import com.example.housekeeping.common.BusinessException;
import com.example.housekeeping.dto.request.RechargeCreateRequest;
import com.example.housekeeping.entity.RechargeRecord;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.enums.RechargeStatus;
import com.example.housekeeping.repository.RechargeRecordRepository;
import com.example.housekeeping.repository.UserRepository;
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
@RequestMapping("/api/user/recharges")
@RequiredArgsConstructor
public class UserRechargeController {

    private final RechargeRecordRepository rechargeRecordRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ApiResponse<Page<RechargeRecord>> list(@RequestParam Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return ApiResponse.success(rechargeRecordRepository.findByUser(user, pageable));
    }

    @PostMapping
    public ApiResponse<RechargeRecord> create(@RequestBody @Valid RechargeCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("用户不存在"));
        RechargeRecord record = new RechargeRecord();
        record.setUser(user);
        record.setAmount(request.getAmount());
        record.setPaymentMethod(request.getPaymentMethod());
        record.setStatus(RechargeStatus.PENDING);
        return ApiResponse.success(rechargeRecordRepository.save(record));
    }
}
