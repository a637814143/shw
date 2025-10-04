package com.example.housekeeping.controller;

import com.example.housekeeping.model.UserAccount;
import com.example.housekeeping.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {

    private final AuthService authService;

    public WalletController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> wallet(Authentication authentication) {
        UserAccount account = authService.findByUsername(authentication.getName());
        Map<String, Object> response = new HashMap<>();
        response.put("userName", account.getUserName());
        response.put("types", account.getTypes());
        response.put("money", account.getMoney());
        response.put("role", extractRole(authentication));
        return ResponseEntity.ok(response);
    }

    private String extractRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");
    }
}
