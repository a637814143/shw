package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.CompanyMessageRequest;
import com.example.housekeeping.dto.CompanyMessageResponse;
import com.example.housekeeping.dto.UserConversationSummaryResponse;
import com.example.housekeeping.service.UserMessagingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * 普通用户客服沟通接口。
 */
@RestController
@RequestMapping("/api/user/messages")
public class UserMessagingController {

    @Autowired
    private UserMessagingService userMessagingService;

    @GetMapping("/threads")
    public Result<List<UserConversationSummaryResponse>> listThreads() {
        return Result.success(userMessagingService.listConversationSummaries());
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
        return Result.success(userMessagingService.listMessages(orderId, instant));
    }

    @PostMapping("/orders/{orderId}")
    public Result<CompanyMessageResponse> sendMessage(@PathVariable Long orderId,
                                                      @Valid @RequestBody CompanyMessageRequest request) {
        return Result.success("发送成功", userMessagingService.sendMessage(orderId, request));
    }

    @PostMapping("/orders/{orderId}/read")
    public Result<Void> markRead(@PathVariable Long orderId) {
        userMessagingService.markRead(orderId);
        return Result.success("已读", null);
    }
}
