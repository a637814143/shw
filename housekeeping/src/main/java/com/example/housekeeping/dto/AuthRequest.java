package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.RoleType;
public record AuthRequest(
        String username,
        String password,
        RoleType role
) {}
