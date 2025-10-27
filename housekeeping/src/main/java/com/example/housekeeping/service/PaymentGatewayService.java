package com.example.housekeeping.service;

import com.example.housekeeping.dto.PaymentCheckResponse;
import com.example.housekeeping.enums.PaymentCheckStatus;
import com.example.housekeeping.model.QrPaymentSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的扫码支付会话管理。
 */
@Service
public class PaymentGatewayService {

    private static final Logger log = LoggerFactory.getLogger(PaymentGatewayService.class);

    private static final Duration SESSION_TTL = Duration.ofMinutes(10);

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, QrPaymentSession> sessions = new ConcurrentHashMap<>();

    public QrPaymentSession createSession(String serviceName, String companyName, BigDecimal amount) {
        purgeExpiredSessions();
        String token = generateToken();
        Instant now = Instant.now();
        QrPaymentSession session = new QrPaymentSession(
            token,
            now,
            now.plus(SESSION_TTL),
            serviceName,
            companyName,
            amount
        );
        sessions.put(token, session);
        return session;
    }

    public Optional<QrPaymentSession> findSession(String token) {
        purgeExpiredSessions();
        return Optional.ofNullable(token).map(sessions::get);
    }

    public PaymentCheckResponse checkSessionStatus(String token) {
        if (token == null || token.isBlank()) {
            return PaymentCheckResponse.builder()
                .status(PaymentCheckStatus.ERROR)
                .message("支付会话参数缺失，请重新生成二维码。")
                .build();
        }
        purgeExpiredSessions();
        QrPaymentSession session = sessions.get(token);
        if (session == null) {
            return PaymentCheckResponse.builder()
                .status(PaymentCheckStatus.ERROR)
                .message("支付会话不存在或已过期，请重新生成二维码。")
                .token(token)
                .build();
        }

        Instant now = Instant.now();
        if (session.tryExpire(now)) {
            log.info("支付会话 {} 已过期", token);
        }

        return buildResponse(session);
    }

    public PaymentCheckResponse applyDecision(String token, boolean confirmed) {
        purgeExpiredSessions();
        QrPaymentSession session = sessions.get(token);
        if (session == null) {
            return PaymentCheckResponse.builder()
                .status(PaymentCheckStatus.ERROR)
                .message("支付会话不存在或已过期，请返回平台重新发起支付。")
                .token(token)
                .build();
        }

        Instant now = Instant.now();
        PaymentCheckStatus status = confirmed ? session.markConfirmed(now) : session.markDeclined(now);
        return buildResponse(session, status);
    }

    private PaymentCheckResponse buildResponse(QrPaymentSession session) {
        return buildResponse(session, session.getStatus());
    }

    private PaymentCheckResponse buildResponse(QrPaymentSession session, PaymentCheckStatus status) {
        String message;
        switch (status) {
            case CONFIRMED -> message = "支付已确认。";
            case DECLINED -> message = session.isExpired(Instant.now())
                ? "支付会话已过期，请重新发起。"
                : "支付未完成或已取消，请重新发起。";
            case ERROR -> message = "支付服务调用失败，请稍后重试。";
            default -> message = "暂未获取到支付结果，请在扫码页面确认后再试。";
        }

        return PaymentCheckResponse.builder()
            .status(status)
            .message(message)
            .rawPayload(null)
            .token(session.getToken())
            .expiresAt(session.getExpiresAt())
            .build();
    }

    public void consumeSession(String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        sessions.remove(token);
    }

    private void purgeExpiredSessions() {
        Instant now = Instant.now();
        sessions.entrySet().removeIf(entry -> {
            QrPaymentSession session = entry.getValue();
            if (session.isExpired(now) && session.getStatus() != PaymentCheckStatus.CONFIRMED) {
                log.debug("移除过期支付会话 {}", entry.getKey());
                return true;
            }
            return false;
        });
    }

    private String generateToken() {
        byte[] bytes = new byte[18];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
