package com.example.housekeeping.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AppointmentCreateRequest(
        Long serviceId,
        LocalDateTime scheduledAt,
        String address,
        String contactName,
        String contactPhone,
        BigDecimal price,
        String userNotes
) {}
