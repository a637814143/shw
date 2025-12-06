package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.AssignStaffRequest;
import com.example.housekeeping.dto.CompanyStaffRequest;
import com.example.housekeeping.dto.CompanyStaffResponse;
import com.example.housekeeping.dto.IdListRequest;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.service.CompanyStaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 家政公司人员管理相关接口。
 */
@RestController
@RequestMapping("/api/company/staff")
public class CompanyStaffController {

    @Autowired
    private CompanyStaffService companyStaffService;

    @GetMapping
    public Result<List<CompanyStaffResponse>> listStaff(@RequestParam(value = "keyword", required = false) String keyword,
                                                        @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                        @RequestParam(value = "scheduledAt", required = false) String scheduledAt) {
        java.time.Instant slotInstant = null;
        if (scheduledAt != null && !scheduledAt.isBlank()) {
            try {
                slotInstant = java.time.Instant.parse(scheduledAt.trim());
            } catch (Exception ex) {
                throw new RuntimeException("预约时间格式无效");
            }
        }
        return Result.success(companyStaffService.listStaff(keyword, categoryId, slotInstant));
    }

    @PostMapping
    public Result<CompanyStaffResponse> createStaff(@Valid @RequestBody CompanyStaffRequest request) {
        return Result.success("人员已创建", companyStaffService.createStaff(request));
    }

    @PutMapping("/{staffId}")
    public Result<CompanyStaffResponse> updateStaff(@PathVariable Long staffId,
                                                    @Valid @RequestBody CompanyStaffRequest request) {
        return Result.success("人员信息已更新", companyStaffService.updateStaff(staffId, request));
    }

    @DeleteMapping("/{staffId}")
    public Result<Void> deleteStaff(@PathVariable Long staffId) {
        companyStaffService.deleteStaff(staffId);
        return Result.success("人员已删除", null);
    }

    @DeleteMapping("/batch")
    public Result<Void> deleteStaffBatch(@Valid @RequestBody IdListRequest request) {
        companyStaffService.deleteStaff(request.getIds());
        return Result.success("已删除所选人员", null);
    }

    @PostMapping("/{staffId}/assign")
    public Result<ServiceOrderResponse> assignStaff(@PathVariable Long staffId,
                                                    @Valid @RequestBody AssignStaffRequest request) {
        return Result.success("已指派人员", companyStaffService.assignStaff(staffId, request));
    }
}
