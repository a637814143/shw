package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotNull;

public record AppointmentAssignmentRequest(
        @NotNull Long providerId,
        String adminNotes
) {}
