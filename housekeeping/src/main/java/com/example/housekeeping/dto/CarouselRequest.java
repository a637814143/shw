package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CarouselRequest(
        @NotBlank String title,
        @NotBlank String imageUrl,
        String linkUrl,
        Long serviceId,
        @NotNull Integer sortOrder,
        boolean enabled
) {}
