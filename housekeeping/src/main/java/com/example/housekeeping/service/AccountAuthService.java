package com.example.housekeeping.service;

import com.example.housekeeping.dto.AccountLoginRequest;
import com.example.housekeeping.dto.AccountRegisterRequest;
import com.example.housekeeping.dto.AccountSummary;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.entity.AuthAccount;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.AuthAccountRepository;
import com.example.housekeeping.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 公共账号认证服务
 */
@Service
public class AccountAuthService {

    @Autowired
    private AuthAccountRepository authAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AccountSummary register(AccountRegisterRequest request) {
        String normalizedAccount = request.getAccount().trim();
        if (authAccountRepository.existsByAccount(normalizedAccount)) {
            throw new RuntimeException("账号已存在");
        }

        AccountRole role = AccountRole.fromValue(request.getRole());

        AuthAccount account = new AuthAccount();
        account.setAccount(normalizedAccount);
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setRole(role);

        AuthAccount saved = authAccountRepository.save(account);
        return new AccountSummary(saved.getId(), saved.getAccount(), role.getValue());
    }

    public LoginResponse login(AccountLoginRequest request) {
        String normalizedAccount = request.getAccount().trim();
        AuthAccount account = authAccountRepository.findByAccount(normalizedAccount)
                .orElseThrow(() -> new RuntimeException("账号或密码错误"));

        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }

        AccountRole requestedRole = AccountRole.fromValue(request.getRole());
        if (account.getRole() != requestedRole) {
            throw new RuntimeException("角色不匹配，请选择正确的角色登录");
        }

        String token = jwtUtil.generateToken(account.getAccount(), account.getRole().name());
        return new LoginResponse(token, account.getAccount(), account.getAccount(), account.getRole().name());
    }
}
