package com.example.housekeeping.dto;

public record ServiceCategoryRequest(
        String name,
        String description,
        String iconUrl,
        Integer sortOrder,
        boolean enabled
) {}
