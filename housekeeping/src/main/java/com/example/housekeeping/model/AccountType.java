package com.example.housekeeping.model;

/**
 * 系统支持的账号类型。
 */
public enum AccountType {
    ADMIN,
    USER,
    PROVIDER;

    /**
     * 将任意字符串安全转换为 {@link AccountType}。
     *
     * @param rawType 前端传入的类型字符串
     * @return 枚举值
     * @throws IllegalArgumentException 当类型不在预期范围内时抛出
     */
    public static AccountType from(String rawType) {
        if (rawType == null) {
            throw new IllegalArgumentException("账号类型不能为空");
        }

        try {
            return AccountType.valueOf(rawType.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("不支持的账号类型: " + rawType);
        }
    }
}

