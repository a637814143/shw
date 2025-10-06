package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.RoleType;
import java.time.LocalDateTime;

public record AuthResponse(
        Long id,
        RoleType role,
        String username,
        String displayName,
        LocalDateTime loginTime
) {}
