package com.example.housekeeping.dto;

import java.math.BigDecimal;

public record HousekeepingServiceRequest(
        Long categoryId,
        String title,
        String description,
        BigDecimal price,
        Integer durationMinutes,
        boolean featured,
        String coverImageUrl,
        String tags,
        boolean enabled
) {}
