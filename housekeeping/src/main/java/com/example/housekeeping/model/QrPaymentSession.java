package com.example.housekeeping.model;

import com.example.housekeeping.enums.PaymentCheckStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 简单的扫码支付会话，使用内存存储。
 */
public class QrPaymentSession {

    private final String token;
    private final Instant createdAt;
    private final Instant expiresAt;
    private final String serviceName;
    private final String companyName;
    private final BigDecimal amount;
    private final AtomicReference<PaymentCheckStatus> status;
    private final AtomicReference<Instant> decidedAt;

    public QrPaymentSession(
        String token,
        Instant createdAt,
        Instant expiresAt,
        String serviceName,
        String companyName,
        BigDecimal amount
    ) {
        this.token = Objects.requireNonNull(token, "token");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt");
        this.serviceName = serviceName;
        this.companyName = companyName;
        this.amount = amount;
        this.status = new AtomicReference<>(PaymentCheckStatus.PENDING);
        this.decidedAt = new AtomicReference<>(null);
    }

    public String getToken() {
        return token;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Optional<String> getServiceName() {
        return Optional.ofNullable(serviceName);
    }

    public Optional<String> getCompanyName() {
        return Optional.ofNullable(companyName);
    }

    public Optional<BigDecimal> getAmount() {
        return Optional.ofNullable(amount);
    }

    public PaymentCheckStatus getStatus() {
        return status.get();
    }

    public Optional<Instant> getDecidedAt() {
        return Optional.ofNullable(decidedAt.get());
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean tryExpire(Instant now) {
        if (!isExpired(now)) {
            return false;
        }
        return status.compareAndSet(PaymentCheckStatus.PENDING, PaymentCheckStatus.DECLINED);
    }

    public PaymentCheckStatus markConfirmed(Instant now) {
        return updateStatus(now, PaymentCheckStatus.CONFIRMED);
    }

    public PaymentCheckStatus markDeclined(Instant now) {
        return updateStatus(now, PaymentCheckStatus.DECLINED);
    }

    private PaymentCheckStatus updateStatus(Instant now, PaymentCheckStatus target) {
        PaymentCheckStatus current = status.get();
        if (current == PaymentCheckStatus.CONFIRMED || current == PaymentCheckStatus.DECLINED) {
            return current;
        }
        if (status.compareAndSet(PaymentCheckStatus.PENDING, target)) {
            decidedAt.set(now);
            return target;
        }
        return status.get();
    }
}
