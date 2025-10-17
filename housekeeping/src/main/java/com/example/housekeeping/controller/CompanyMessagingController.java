package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.CompanyConversationSummaryResponse;
import com.example.housekeeping.dto.CompanyMessageRequest;
import com.example.housekeeping.dto.CompanyMessageResponse;
import com.example.housekeeping.service.CompanyMessagingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * 企业端客户沟通接口。
 */
@RestController
@RequestMapping("/api/company/messages")
public class CompanyMessagingController {

    @Autowired
    private CompanyMessagingService companyMessagingService;

    @GetMapping("/threads")
    public Result<List<CompanyConversationSummaryResponse>> listThreads() {
        return Result.success(companyMessagingService.listConversationSummaries());
    }

    @GetMapping("/orders/{orderId}")
    public Result<List<CompanyMessageResponse>> listMessages(@PathVariable Long orderId,
                                                             @RequestParam(name = "since", required = false) String since) {
        Instant instant = null;
        if (since != null && !since.isBlank()) {
            try {
                instant = Instant.parse(since);
            } catch (Exception ex) {
                throw new RuntimeException("时间格式不正确，请使用ISO-8601格式", ex);
            }
        }
        return Result.success(companyMessagingService.listMessages(orderId, instant));
    }

    @PostMapping("/orders/{orderId}")
    public Result<CompanyMessageResponse> sendMessage(@PathVariable Long orderId,
                                                      @Valid @RequestBody CompanyMessageRequest request) {
        return Result.success("消息已发送", companyMessagingService.sendMessage(orderId, request));
    }

    @PostMapping("/orders/{orderId}/read")
    public Result<Void> markRead(@PathVariable Long orderId) {
        companyMessagingService.markConversationRead(orderId);
        return Result.success("已标记已读", null);
    }
}
