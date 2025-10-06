package com.example.housekeeping.dto;

public record UpdateProfileRequest(
        String fullName,
        String phone,
        String email,
        String address,
        String avatarUrl,
        String biography,
        String serviceArea,
        String skills
) {}
