package com.example.housekeeping.enums;

import java.util.Arrays;

/**
 * 系统登录账号角色枚举
 */
public enum AccountRole {
    ADMIN("admin", "系统管理员"),
    COMPANY("company", "家政公司"),
    USER("user", "普通用户");

    private final String code;
    private final String label;

    AccountRole(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static AccountRole fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("角色不能为空");
        }
        String normalized = value.trim();
        return Arrays.stream(AccountRole.values())
                .filter(role -> role.code.equalsIgnoreCase(normalized) || role.label.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("不支持的角色类型: " + value));
    }
}
