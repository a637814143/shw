package com.example.housekeeping.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewCreateRequest {

    @NotNull
    private Long bookingId;

    @NotNull
    private Long userId;

    @NotNull
    private Long serviceId;

    @NotNull
    private Long providerId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String content;

    private String images;
}
