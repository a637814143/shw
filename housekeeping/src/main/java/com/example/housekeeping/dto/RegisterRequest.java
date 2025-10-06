package com.example.housekeeping.dto;

import com.example.housekeeping.domain.enums.RoleType;
public record RegisterRequest(
        String username,
        String password,
        RoleType role,
        String fullName,
        String phone,
        String email,
        String address
) {}
