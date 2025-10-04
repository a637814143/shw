package com.example.housekeeping.controller;

import com.example.housekeeping.model.UserAccount;
import com.example.housekeeping.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final AuthService authService;

    public WalletController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public Map<String, Object> getWallet(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        UserAccount account = authService.findByUsername(authentication.getName());
        Map<String, Object> response = new HashMap<>();
        response.put("userName", account.getUserName());
        response.put("types", account.getTypes());
        response.put("money", account.getMoney());
        response.put("role", account.getTypes());
        return response;
    }
}
