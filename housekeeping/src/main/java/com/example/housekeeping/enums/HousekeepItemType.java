package com.example.housekeeping.enums;

/**
 * 家政内容类型
 */
public enum HousekeepItemType {
    SERVICE("service"),
    TIP("tip"),
    REVIEW("review"),
    OFFER("offer");

    private final String value;

    HousekeepItemType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
