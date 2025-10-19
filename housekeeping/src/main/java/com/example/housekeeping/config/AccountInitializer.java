package com.example.housekeeping.config;

import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.UserAllRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 初始化系统管理员账号
 */
@Component
public class AccountInitializer implements ApplicationRunner {

    private final UserAllRepository userAllRepository;

    public AccountInitializer(UserAllRepository userAllRepository) {
        this.userAllRepository = userAllRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        userAllRepository.findByUsername("admin").ifPresentOrElse(
                existing -> {
                    existing.setUserType(AccountRole.ADMIN.getLabel());
                    if (existing.getPasswd() == null || existing.getPasswd().isBlank()) {
                        existing.setPasswd(encodePassword("admin"));
                    }
                    if (existing.getMoney() == null) {
                        existing.setMoney(new BigDecimal("1000.00"));
                    }
                    if (existing.getLoyaltyPoints() == null) {
                        existing.setLoyaltyPoints(0);
                    }
                    if (existing.getDisplayName() == null || existing.getDisplayName().isBlank()) {
                        existing.setDisplayName("系统管理员");
                    }
                    userAllRepository.save(existing);
                },
                () -> {
                    UserAll admin = new UserAll();
                    admin.setUsername("admin");
                    admin.setPasswd(encodePassword("admin"));
                    admin.setMoney(new BigDecimal("1000.00"));
                    admin.setLoyaltyPoints(0);
                    admin.setUserType(AccountRole.ADMIN.getLabel());
                    admin.setDisplayName("系统管理员");
                    userAllRepository.save(admin);
                }
        );
    }

    private String encodePassword(String rawPassword) {
        String first = Base64.getEncoder().encodeToString(rawPassword.getBytes(StandardCharsets.UTF_8));
        String second = Base64.getEncoder().encodeToString(first.getBytes(StandardCharsets.UTF_8));
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
