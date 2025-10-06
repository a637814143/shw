package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;

public record CertificationSubmissionRequest(
        @NotBlank String documents,
        String experienceYears,
        String speciality
) {}
