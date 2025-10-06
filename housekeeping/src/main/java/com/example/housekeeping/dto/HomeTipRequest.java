package com.example.housekeeping.dto;

public record HomeTipRequest(
        String title,
        String summary,
        String content,
        String coverImageUrl,
        boolean featured
) {}
