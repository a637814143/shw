package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record HousekeepingServiceRequest(
        @NotNull Long categoryId,
        @NotBlank String title,
        String description,
        @NotNull @Positive BigDecimal price,
        Integer durationMinutes,
        boolean featured,
        String coverImageUrl,
        String tags,
        boolean enabled
) {}
