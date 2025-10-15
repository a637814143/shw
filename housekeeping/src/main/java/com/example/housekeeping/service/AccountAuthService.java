package com.example.housekeeping.service;

import com.example.housekeeping.dto.AccountLoginRequest;
import com.example.housekeeping.dto.AccountRegisterRequest;
import com.example.housekeeping.dto.AccountSummary;
import com.example.housekeeping.dto.LoginResponse;
import com.example.housekeeping.entity.AuthAccount;
import com.example.housekeeping.entity.User;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.AuthAccountRepository;
import com.example.housekeeping.repository.UserRepository;
import com.example.housekeeping.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public AccountSummary register(AccountRegisterRequest request) {
        String normalizedAccount = request.getAccount().trim();
        if (authAccountRepository.existsByAccount(normalizedAccount)) {
            throw new RuntimeException("账号已存在");
        }

        AccountRole role = AccountRole.fromValue(request.getRole());

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        if (role == AccountRole.USER) {
            createUserProfile(request, normalizedAccount, encodedPassword);
        }

        AuthAccount account = new AuthAccount();
        account.setAccount(normalizedAccount);
        account.setPassword(encodedPassword);
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
        String displayName = resolveDisplayName(account);
        return new LoginResponse(token, account.getAccount(), displayName, account.getRole().name());
    }

    private void createUserProfile(AccountRegisterRequest request, String normalizedAccount, String encodedPassword) {
        String realName = Optional.ofNullable(request.getRealName())
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .orElseThrow(() -> new RuntimeException("请填写真实姓名"));

        String phone = Optional.ofNullable(request.getPhone())
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElseThrow(() -> new RuntimeException("请填写手机号"));

        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }

        if (userRepository.existsByUsername(normalizedAccount)) {
            throw new RuntimeException("用户名已存在");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new RuntimeException("手机号已存在");
        }

        User user = new User();
        user.setUsername(normalizedAccount);
        user.setPassword(encodedPassword);
        user.setRealName(realName);
        user.setPhone(phone);

        String email = Optional.ofNullable(request.getEmail())
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse(null);
        user.setEmail(email);

        userRepository.save(user);
    }

    private String resolveDisplayName(AuthAccount account) {
        if (account.getRole() == AccountRole.USER) {
            return userRepository.findByUsernameAndStatus(account.getAccount(), 1)
                    .map(User::getRealName)
                    .orElseThrow(() -> new RuntimeException("用户资料不存在或已被禁用"));
        }
        return account.getAccount();
    }
}
