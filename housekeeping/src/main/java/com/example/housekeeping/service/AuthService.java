package com.example.housekeeping.service;

import com.example.housekeeping.dto.LoginRequest;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.dto.RegisterRequest;
import com.example.housekeeping.entity.UserForHousekeeper;
import com.example.housekeeping.exception.InvalidCredentialsException;
import com.example.housekeeping.exception.UsernameAlreadyExistsException;
import com.example.housekeeping.repository.UserForHousekeeperRepository;
import com.example.housekeeping.util.PasswordTransformer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final UserForHousekeeperRepository repository;

    private static final BigDecimal DEFAULT_USER_MONEY = BigDecimal.valueOf(100);

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

        BigDecimal balance = user.getUsermoney() != null ? user.getUsermoney() : DEFAULT_USER_MONEY;

        return new LoginResponse(user.getUsername(), user.getUsertype(), balance);
    }

    @Transactional
    public void register(RegisterRequest request) {
        repository.findByUsername(request.getUsername()).ifPresent(user -> {
            throw new UsernameAlreadyExistsException("用户名已存在");
        });

        UserForHousekeeper user = new UserForHousekeeper();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordTransformer.encode(request.getPassword()));
        user.setUsertype(request.getUsertype());
        user.setUsermoney(DEFAULT_USER_MONEY);

        repository.save(user);
    }
}
