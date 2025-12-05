package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.HousekeepServiceResponse;
import com.example.housekeeping.dto.ServiceApprovalRequest;
import com.example.housekeeping.enums.HousekeepServiceStatus;
import com.example.housekeeping.service.HousekeepServiceManager;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
public class AdminServiceApprovalController {

    @Autowired
    private HousekeepServiceManager housekeepServiceManager;

    @GetMapping("/approval")
    public Result<List<HousekeepServiceResponse>> listForApproval(
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "categoryId", required = false) Long categoryId,
        @RequestParam(value = "status", required = false) String status
    ) {
        HousekeepServiceStatus statusEnum = parseStatus(status);
        return Result.success(housekeepServiceManager.listForAdmin(keyword, categoryId, statusEnum));
    }

    @PostMapping("/{serviceId}/review")
    public Result<HousekeepServiceResponse> review(
        @PathVariable Long serviceId,
        @Valid @RequestBody ServiceApprovalRequest request
    ) {
        return Result.success(
            "操作成功",
            housekeepServiceManager.reviewService(serviceId, request.getApprove(), request.getReason())
        );
    }

    private HousekeepServiceStatus parseStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return HousekeepServiceStatus.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
