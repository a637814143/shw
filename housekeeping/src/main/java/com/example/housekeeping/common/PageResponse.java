package com.example.housekeeping.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wrapper for paginated responses.
 */
@Data
public class PageResponse<T> {

    private final long totalElements;
    private final int totalPages;
    private final int pageNumber;
    private final int pageSize;
    private final List<T> records;

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getTotalElements(), page.getTotalPages(), page.getNumber(), page.getSize(), page.getContent());
    }
}
