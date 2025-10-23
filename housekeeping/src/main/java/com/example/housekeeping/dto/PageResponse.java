package com.example.housekeeping.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通用分页响应。
 */
public class PageResponse<T> {

    private final List<T> items;
    private final long total;
    private final int page;
    private final int size;

    public PageResponse(List<T> items, long total, int page, int size) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public static <T> PageResponse<T> empty(int size) {
        int safeSize = clampSize(size);
        return new PageResponse<>(Collections.emptyList(), 0, 1, safeSize);
    }

    public static <T> PageResponse<T> from(List<T> source, int page, int size) {
        if (source == null || source.isEmpty()) {
            return empty(size);
        }
        int safeSize = clampSize(size);
        int safePage = Math.max(page, 1);
        long total = source.size();
        int totalPages = (int) Math.ceil(total / (double) safeSize);
        if (totalPages <= 0) {
            totalPages = 1;
        }
        if (safePage > totalPages) {
            safePage = totalPages;
        }
        int fromIndex = Math.min((safePage - 1) * safeSize, source.size());
        int toIndex = Math.min(fromIndex + safeSize, source.size());
        List<T> items = new ArrayList<>(source.subList(fromIndex, toIndex));
        return new PageResponse<>(items, total, safePage, safeSize);
    }

    private static int clampSize(int size) {
        int safeSize = size <= 0 ? 5 : size;
        return Math.min(safeSize, 5);
    }

    public List<T> getItems() {
        return items;
    }

    public long getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}

