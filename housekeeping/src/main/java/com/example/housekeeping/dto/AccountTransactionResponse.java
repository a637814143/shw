package com.example.housekeeping.dto;

import com.example.housekeeping.enums.AccountTransactionType;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * 账户交易流水。
 */
public class AccountTransactionResponse {

    private final Long id;
    private final String username;
    private final AccountTransactionType type;
    private final BigDecimal amount;
    private final String note;
    private final Instant createdAt;

    public AccountTransactionResponse(Long id, String username, AccountTransactionType type,
                                      BigDecimal amount, String note, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.type = type;
        this.amount = amount;
        this.note = note;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public AccountTransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
