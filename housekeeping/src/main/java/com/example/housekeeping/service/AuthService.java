package com.example.housekeeping.service;

import com.example.housekeeping.dto.LoginRequest;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.entity.UserForHousekeeper;
import com.example.housekeeping.exception.InvalidCredentialsException;
import com.example.housekeeping.repository.UserForHousekeeperRepository;
import com.example.housekeeping.util.PasswordTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserForHousekeeperRepository repository;

    public AuthService(UserForHousekeeperRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UserForHousekeeper user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("账号或密码错误"));

        if (!user.getUsertype().equalsIgnoreCase(request.getUsertype())) {
            throw new InvalidCredentialsException("账号或角色不匹配");
        }

        if (!PasswordTransformer.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("账号或密码错误");
        }

        return new LoginResponse(user.getUsername(), user.getUsertype(), user.getUsermoney());
    }
}
