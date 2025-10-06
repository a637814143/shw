package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotNull RoleType role
) {}
