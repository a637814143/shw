package com.example.housekeeping.service;

import com.example.housekeeping.dto.AccountLoginRequest;
import com.example.housekeeping.dto.AccountRegisterRequest;
import com.example.housekeeping.dto.AccountSummary;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.UserAllRepository;
import com.example.housekeeping.util.JwtUtil;
import com.example.housekeeping.util.LegacyPasswordEncoder;
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

    @Autowired
    private LegacyPasswordEncoder legacyPasswordEncoder;

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
        account.setPasswd(legacyPasswordEncoder.encode(request.getPassword()));
        account.setMoney(DEFAULT_BALANCE);
        account.setLoyaltyPoints(0);
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

        if (!account.getPasswd().equals(legacyPasswordEncoder.encode(request.getPassword()))) {
            throw new RuntimeException("账号或密码错误");
        }

        String token = jwtUtil.generateToken(account.getUsername(), requestedRole.getCode());
        return new LoginResponse(token, account.getUsername(), account.getUsername(), requestedRole.getCode());
    }

}
