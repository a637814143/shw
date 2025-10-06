package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.AnnouncementTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnnouncementRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull AnnouncementTarget target,
        boolean pinned,
        boolean enabled,
        String publishedBy
) {}
