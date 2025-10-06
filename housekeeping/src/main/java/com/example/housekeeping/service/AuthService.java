package com.example.housekeeping.service;

import com.example.housekeeping.domain.enums.RoleType;
import com.example.housekeeping.dto.AuthRequest;
import com.example.housekeeping.dto.AuthResponse;
import com.example.housekeeping.dto.RegisterRequest;
import com.example.housekeeping.dto.UpdatePasswordRequest;
import com.example.housekeeping.dto.UpdateProfileRequest;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    AuthResponse register(RegisterRequest request);

    void updatePassword(RoleType role, Long id, UpdatePasswordRequest request);

    void updateProfile(RoleType role, Long id, UpdateProfileRequest request);
}
