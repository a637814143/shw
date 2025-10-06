package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.UserAccount;
import com.example.housekeeping.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserAccountRepository userAccountRepository;

    public UserManagementController(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public List<UserAccount> listUsers() {
        return userAccountRepository.findAll();
    }

    @GetMapping("/{id}")
    public UserAccount getUser(@PathVariable Long id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在"));
    }

    @PutMapping("/{id}/enabled")
    public UserAccount updateUserStatus(@PathVariable Long id, @RequestParam boolean enabled) {
        UserAccount user = getUser(id);
        user.setEnabled(enabled);
        return user;
    }

    @PutMapping("/{id}/balance")
    public UserAccount adjustBalance(@PathVariable Long id, @RequestParam BigDecimal balance) {
        UserAccount user = getUser(id);
        user.setBalance(balance);
        return user;
    }
}
