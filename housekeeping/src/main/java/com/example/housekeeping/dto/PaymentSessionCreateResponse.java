package com.example.housekeeping.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * 返回给前端的扫码支付会话信息。
 */
@Data
@Builder
public class PaymentSessionCreateResponse {

    private String token;

    private String qrPath;

    private String qrUrl;

    private Instant expiresAt;
}
