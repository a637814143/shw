package com.example.housekeeping.dto;

public record CarouselRequest(
        String title,
        String imageUrl,
        String linkUrl,
        Long serviceId,
        Integer sortOrder,
        boolean enabled
) {}
