package com.example.housekeeping.dto;

public record UpdatePasswordRequest(
        String oldPassword,
        String newPassword
) {}
