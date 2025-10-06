package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.AppointmentStatus;
public record AppointmentStatusUpdateRequest(
        AppointmentStatus status,
        String remark
) {}
