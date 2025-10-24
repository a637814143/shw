package com.example.housekeeping.enums;

/**
 * 家政公司人员当前工作状态。
 */
public enum StaffStatus {

    IDLE("空闲"),
    BUSY("繁忙");

    private final String displayName;

    StaffStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
