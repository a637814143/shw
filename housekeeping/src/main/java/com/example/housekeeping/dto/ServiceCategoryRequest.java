package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;

public record ServiceCategoryRequest(
        @NotBlank String name,
        String description,
        String iconUrl,
        Integer sortOrder,
        boolean enabled
) {}
