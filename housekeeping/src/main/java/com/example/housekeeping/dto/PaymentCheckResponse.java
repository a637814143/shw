package com.example.housekeeping.dto;

import com.example.housekeeping.enums.PaymentCheckStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户扫码支付结果的响应体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCheckResponse {

    /**
     * 支付结果状态。
     */
    private PaymentCheckStatus status;

    /**
     * 展示给用户的提示信息。
     */
    private String message;

    /**
     * 外部服务返回的原始内容，便于诊断问题。
     */
    private String rawPayload;
}

