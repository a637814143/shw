package com.example.housekeeping.service;

import com.example.housekeeping.dto.request.LoginRequest;
import com.example.housekeeping.dto.request.RegisterRequest;
import com.example.housekeeping.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse loginAdmin(LoginRequest request);

    LoginResponse loginProvider(LoginRequest request);

    LoginResponse loginUser(LoginRequest request);

    LoginResponse registerUser(RegisterRequest request);

    LoginResponse registerProvider(RegisterRequest request);
}
