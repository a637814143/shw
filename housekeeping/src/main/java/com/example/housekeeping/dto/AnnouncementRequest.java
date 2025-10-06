package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.AnnouncementTarget;

public record AnnouncementRequest(
        String title,
        String content,
        AnnouncementTarget target,
        boolean pinned,
        boolean enabled,
        String publishedBy
) {}
