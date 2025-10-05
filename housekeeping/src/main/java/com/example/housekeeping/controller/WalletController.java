package com.example.housekeeping.controller;

import com.example.housekeeping.model.UserAccount;
import com.example.housekeeping.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final AuthService authService;

    public WalletController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getWallet(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (!StringUtils.hasText(username)) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getName() != null) {
                username = authentication.getName();
            }
        }

        if (!StringUtils.hasText(username)) {
            return ResponseEntity.status(401).body(Map.of("message", "未登录或登录信息已过期"));
        }

        UserAccount account = authService.findByUsername(username);
        Map<String, Object> payload = Map.of(
                "userName", account.getUserName(),
                "types", account.getTypes().name(),
                "money", account.getMoney(),
                "role", account.getTypes().name()
        );
        return ResponseEntity.ok(payload);
    }
}

