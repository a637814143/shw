package com.example.housekeeping.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentCreateRequest(
        @NotNull Long serviceId,
        @NotNull @FutureOrPresent LocalDateTime scheduledAt,
        @NotBlank String address,
        @NotBlank String contactName,
        @NotBlank String contactPhone,
        BigDecimal price,
        String userNotes
) {}
