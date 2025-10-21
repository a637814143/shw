package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.PaymentCheckResponse;
import com.example.housekeeping.dto.PaymentSessionCreateRequest;
import com.example.housekeeping.dto.PaymentSessionCreateResponse;
import com.example.housekeeping.model.QrPaymentSession;
import com.example.housekeeping.service.PaymentGatewayService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * 用户扫码支付相关接口。
 */
@RestController
@RequestMapping("/api/user/payments")
public class UserPaymentController {

    private final PaymentGatewayService paymentGatewayService;

    public UserPaymentController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @PostMapping("/qr/session")
    public Result<PaymentSessionCreateResponse> createQrPaymentSession(
        @Valid @RequestBody PaymentSessionCreateRequest request
    ) {
        QrPaymentSession session = paymentGatewayService.createSession(
            request.getServiceName(),
            request.getCompanyName(),
            request.getAmount()
        );
        String path = "/api/public/payments/qr/" + session.getToken();
        String absoluteUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(path)
            .toUriString();
        PaymentSessionCreateResponse response = PaymentSessionCreateResponse.builder()
            .token(session.getToken())
            .qrPath(path)
            .qrUrl(absoluteUrl)
            .expiresAt(session.getExpiresAt())
            .build();
        return Result.success(response);
    }

    @GetMapping("/qr/status")
    public Result<PaymentCheckResponse> checkQrPaymentStatus(@RequestParam("token") String token) {
        return Result.success(paymentGatewayService.checkSessionStatus(token));
    }
}

