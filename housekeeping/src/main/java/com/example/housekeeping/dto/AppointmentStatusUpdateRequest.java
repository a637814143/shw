package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateRequest(
        @NotNull AppointmentStatus status,
        String remark
) {}
