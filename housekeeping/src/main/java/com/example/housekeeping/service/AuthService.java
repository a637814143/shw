package com.example.housekeeping.service;

import com.example.housekeeping.model.UserAccount;
import com.example.housekeeping.repository.UserAccountRepository;
import com.example.housekeeping.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserAccountRepository userAccountRepository, JwtUtil jwtUtil) {
        this.userAccountRepository = userAccountRepository;
        this.jwtUtil = jwtUtil;
    }

    public UserAccount register(String username, String rawPassword, String type) {
        String normalizedType = normalizeType(type);
        if ("ADMIN".equals(normalizedType)) {
            throw new IllegalArgumentException("管理员账号不支持自行注册");
        }

        if (userAccountRepository.existsByUserName(username)) {
            throw new IllegalStateException("用户名已存在");
        }

        String hashedPassword = encodePassword(rawPassword);
        BigDecimal initialMoney = "USER".equals(normalizedType)
                ? BigDecimal.valueOf(1000)
                : BigDecimal.ZERO;

        UserAccount account = new UserAccount(username, hashedPassword, normalizedType, initialMoney);
        return userAccountRepository.save(account);
    }

    public Map<String, Object> login(String username, String rawPassword) {
        UserAccount account = userAccountRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        String hashedPassword = encodePassword(rawPassword);
        if (!account.getPassword().equals(hashedPassword)) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(null, username, account.getTypes());

        Map<String, Object> userView = new HashMap<>();
        userView.put("userName", account.getUserName());
        userView.put("types", account.getTypes());
        userView.put("money", account.getMoney());

        Map<String, Object> payload = new HashMap<>();
        payload.put("token", token);
        payload.put("user", userView);
        return payload;
    }

    public UserAccount findByUsername(String username) {
        return userAccountRepository.findById(username)
                .orElseThrow(() -> new IllegalArgumentException("账号不存在"));
    }

    public UserAccount save(UserAccount account) {
        return userAccountRepository.save(account);
    }

    private String normalizeType(String type) {
        if (type == null) {
            throw new IllegalArgumentException("必须指定账号类型");
        }
        return type.trim().toUpperCase();
    }

    public String encodePassword(String rawPassword) {
        try {
            String base64 = Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.UTF_8));
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashed = digest.digest(base64.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("密码加密失败", e);
        }
    }
}
