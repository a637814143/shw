package com.example.housekeeping.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 账号概览响应
 */
@Data
@AllArgsConstructor
public class AccountSummary {
    private Long id;
    private String account;
    private String role;
}
