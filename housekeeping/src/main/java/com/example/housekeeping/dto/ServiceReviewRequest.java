package com.example.housekeeping.dto;

public record ServiceReviewRequest(
        Long appointmentId,
        Integer rating,
        String comment
) {}
