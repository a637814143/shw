package com.example.housekeeping.enums;

import java.util.Arrays;

/**
 * 家政服务审核状态。
 */
public enum HousekeepServiceStatus {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "审核通过"),
    REJECTED("REJECTED", "已驳回");

    private final String code;
    private final String label;

    HousekeepServiceStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static HousekeepServiceStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return Arrays.stream(HousekeepServiceStatus.values())
            .filter(status -> status.name().equalsIgnoreCase(normalized)
                || status.code.equalsIgnoreCase(normalized)
                || status.label.equalsIgnoreCase(normalized))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("不支持的服务状态: " + value));
    }
}
