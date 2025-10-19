package com.example.housekeeping.controller;

import com.example.housekeeping.common.Result;
import com.example.housekeeping.dto.PaymentCheckResponse;
import com.example.housekeeping.service.PaymentGatewayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/qr/status")
    public Result<PaymentCheckResponse> checkQrPaymentStatus() {
        return Result.success(paymentGatewayService.checkQrPayment());
    }
}

