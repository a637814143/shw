package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;

public record HomeTipRequest(
        @NotBlank String title,
        String summary,
        String content,
        String coverImageUrl,
        boolean featured
) {}
