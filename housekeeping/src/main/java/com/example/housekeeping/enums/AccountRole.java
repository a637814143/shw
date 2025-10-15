package com.example.housekeeping.enums;

import java.util.Arrays;

/**
 * 系统登录账号角色枚举
 */
public enum AccountRole {
    ADMIN("admin"),
    STAFF("staff"),
    USER("user");

    private final String value;

    AccountRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AccountRole fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("角色不能为空");
        }
        String normalized = value.trim();
        return Arrays.stream(AccountRole.values())
                .filter(role -> role.value.equalsIgnoreCase(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的角色类型: " + value));
    }
}
