package com.example.housekeeping.dto;

public record AppointmentAssignmentRequest(
        Long providerId,
        String adminNotes
) {}
