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

/**
 * 管理员审核家政服务。
 */
@RestController
@RequestMapping("/api/admin/services")
public class AdminServiceApprovalController {

    @Autowired
    private HousekeepServiceManager housekeepServiceManager;

    @GetMapping
    public Result<List<HousekeepServiceResponse>> listServices(@RequestParam(value = "keyword", required = false) String keyword,
                                                               @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                               @RequestParam(value = "status", required = false) String status) {
        HousekeepServiceStatus statusFilter = status == null
            ? HousekeepServiceStatus.PENDING
            : HousekeepServiceStatus.fromValue(status);
        return Result.success(housekeepServiceManager.listForAdmin(keyword, categoryId, statusFilter));
    }

    @PostMapping("/{serviceId}/review")
    public Result<HousekeepServiceResponse> reviewService(@PathVariable Long serviceId,
                                                          @Valid @RequestBody ServiceApprovalRequest request) {
        boolean approve = Boolean.TRUE.equals(request.getApprove());
        return Result.success("操作成功", housekeepServiceManager.reviewService(serviceId, approve, request.getReason()));
    }
}
