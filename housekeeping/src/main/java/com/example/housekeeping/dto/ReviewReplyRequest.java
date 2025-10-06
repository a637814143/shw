package com.example.housekeeping.dto;

import jakarta.validation.constraints.NotBlank;

public record ReviewReplyRequest(
        @NotBlank String reply
) {}
