package com.example.housekeeping.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * 统一的预约时间段解析与冲突判断工具。
 */
public final class ServiceTimeSlotHelper {

    public static final List<SlotDefinition> AVAILABLE_SLOTS = List.of(
        new SlotDefinition("08-10", "08:00-10:00", 8, 0, 10, 0),
        new SlotDefinition("11-13", "11:00-13:00", 11, 0, 13, 0),
        new SlotDefinition("14-16", "14:00-16:00", 14, 0, 16, 0),
        new SlotDefinition("17-19", "17:00-19:00", 17, 0, 19, 0),
        new SlotDefinition("20-22", "20:00-22:00", 20, 0, 22, 0)
    );

    private static final int SLOT_BUFFER_HOURS = 1;

    private ServiceTimeSlotHelper() {
    }

    public static Optional<SlotDefinition> resolveByKey(String rawKey) {
        if (rawKey == null) {
            return Optional.empty();
        }
        String normalized = normalize(rawKey);
        return AVAILABLE_SLOTS.stream()
            .filter(slot -> slot.matches(normalized))
            .findFirst();
    }

    public static Optional<SlotDefinition> resolveByInstant(Instant instant, ZoneId zoneId) {
        if (instant == null) {
            return Optional.empty();
        }
        LocalDateTime local = LocalDateTime.ofInstant(instant, zoneId == null ? ZoneId.systemDefault() : zoneId);
        return AVAILABLE_SLOTS.stream()
            .filter(slot -> slot.getStartHour() == local.getHour() && slot.getStartMinute() == local.getMinute())
            .findFirst();
    }

    public static Instant buildStartInstant(LocalDate date, SlotDefinition slot, ZoneId zoneId) {
        if (date == null || slot == null) {
            return null;
        }
        ZoneId zone = zoneId == null ? ZoneId.systemDefault() : zoneId;
        return ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
            slot.getStartHour(), slot.getStartMinute(), 0, 0, zone).toInstant();
    }

    public static Instant calculateBusyEnd(Instant start, SlotDefinition slot) {
        if (start == null || slot == null) {
            return null;
        }
        Duration slotDuration = slot.getDuration();
        return start.plus(slotDuration).plus(Duration.ofHours(SLOT_BUFFER_HOURS));
    }

    public static boolean overlaps(Instant aStart, SlotDefinition aSlot, Instant bStart, SlotDefinition bSlot) {
        if (aStart == null || bStart == null || aSlot == null || bSlot == null) {
            return false;
        }
        Instant aEnd = calculateBusyEnd(aStart, aSlot);
        Instant bEnd = calculateBusyEnd(bStart, bSlot);
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    public static String normalizeLabel(SlotDefinition slot) {
        if (slot == null) {
            return null;
        }
        return String.format(Locale.CHINA, "%02d:%02d-%02d:%02d", slot.getStartHour(), slot.getStartMinute(),
            slot.getEndHour(), slot.getEndMinute());
    }

    private static String normalize(String raw) {
        return raw.replace("：", ":").replace(" ", "").trim();
    }

    public static final class SlotDefinition {
        private final String key;
        private final String label;
        private final int startHour;
        private final int startMinute;
        private final int endHour;
        private final int endMinute;

        public SlotDefinition(String key, String label, int startHour, int startMinute, int endHour, int endMinute) {
            this.key = key;
            this.label = label;
            this.startHour = startHour;
            this.startMinute = startMinute;
            this.endHour = endHour;
            this.endMinute = endMinute;
        }

        public String getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }

        public int getStartHour() {
            return startHour;
        }

        public int getStartMinute() {
            return startMinute;
        }

        public int getEndHour() {
            return endHour;
        }

        public int getEndMinute() {
            return endMinute;
        }

        public Duration getDuration() {
            LocalDate date = LocalDate.of(2000, 1, 1);
            ZonedDateTime start = ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                startHour, startMinute, 0, 0, ZoneId.systemDefault());
            ZonedDateTime end = ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                endHour, endMinute, 0, 0, ZoneId.systemDefault());
            return Duration.between(start, end);
        }

        boolean matches(String normalizedKey) {
            String standardLabel = normalize(label);
            String compactLabel = String.format(Locale.CHINA, "%d:%02d-%d:%02d", startHour, startMinute, endHour, endMinute);
            String paddedLabel = String.format(Locale.CHINA, "%02d:%02d-%02d:%02d", startHour, startMinute, endHour, endMinute);
            return normalizedKey.equalsIgnoreCase(key)
                || normalizedKey.equalsIgnoreCase(standardLabel)
                || normalizedKey.equalsIgnoreCase(compactLabel)
                || normalizedKey.equalsIgnoreCase(paddedLabel);
        }
    }
}
