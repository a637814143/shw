package com.example.housekeeping.service;

import com.example.housekeeping.dto.AccountLoginRequest;
import com.example.housekeeping.dto.AccountRegisterRequest;
import com.example.housekeeping.dto.AccountSummary;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.UserAllRepository;
import com.example.housekeeping.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 公共账号认证服务
 */
@Service
public class AccountAuthService {

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("1000.00");

    @Autowired
    private UserAllRepository userAllRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public AccountSummary register(AccountRegisterRequest request) {
        String normalizedAccount = request.getAccount().trim();
        if (userAllRepository.existsByUsername(normalizedAccount)) {
            throw new RuntimeException("账号已存在");
        }

        AccountRole role = AccountRole.fromValue(request.getRole());
        if (role == AccountRole.ADMIN) {
            throw new RuntimeException("系统管理员不支持自助注册");
        }

        UserAll account = new UserAll();
        account.setUsername(normalizedAccount);
        account.setPasswd(encodePassword(request.getPassword()));
        account.setMoney(DEFAULT_BALANCE);
        account.setUserType(role.getLabel());

        UserAll saved = userAllRepository.save(account);
        return new AccountSummary(saved.getId(), saved.getUsername(), role.getCode());
    }

    public LoginResponse login(AccountLoginRequest request) {
        String normalizedAccount = request.getAccount().trim();
        UserAll account = userAllRepository.findByUsername(normalizedAccount)
                .orElseThrow(() -> new RuntimeException("账号或密码错误"));

        AccountRole requestedRole = AccountRole.fromValue(request.getRole());

        if (!account.getUserType().equals(requestedRole.getLabel())) {
            throw new RuntimeException("角色不匹配，请选择正确的角色登录");
        }

        if (!account.getPasswd().equals(encodePassword(request.getPassword()))) {
            throw new RuntimeException("账号或密码错误");
        }

        String token = jwtUtil.generateToken(account.getUsername(), requestedRole.getCode());
        return new LoginResponse(token, account.getUsername(), account.getUsername(), requestedRole.getCode());
    }

    private String encodePassword(String rawPassword) {
        String first = java.util.Base64.getEncoder()
                .encodeToString(rawPassword.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        String second = java.util.Base64.getEncoder()
                .encodeToString(first.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return rot13(second);
    }

    private String rot13(String input) {
        StringBuilder builder = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            if (c >= 'a' && c <= 'z') {
                builder.append((char) ((c - 'a' + 13) % 26 + 'a'));
            } else if (c >= 'A' && c <= 'Z') {
                builder.append((char) ((c - 'A' + 13) % 26 + 'A'));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
