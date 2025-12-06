package com.example.housekeeping.dto;

/**
 * 指定日期内服务时间段的可用人员数量。
 */
public class TimeSlotAvailabilityResponse {

    private final String slotKey;
    private final String label;
    private final long availableStaff;
    private final long totalStaff;

    public TimeSlotAvailabilityResponse(String slotKey, String label, long availableStaff, long totalStaff) {
        this.slotKey = slotKey;
        this.label = label;
        this.availableStaff = availableStaff;
        this.totalStaff = totalStaff;
    }

    public String getSlotKey() {
        return slotKey;
    }

    public String getLabel() {
        return label;
    }

    public long getAvailableStaff() {
        return availableStaff;
    }

    public long getTotalStaff() {
        return totalStaff;
    }
}
