package com.example.housekeeping.service;

import com.example.housekeeping.dto.AccountRegisterRequest;
import com.example.housekeeping.dto.AccountSummary;
import com.example.housekeeping.repository.AuthAccountRepository;
import com.example.housekeeping.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AccountAuthServiceTest {

    @Autowired
    private AccountAuthService accountAuthService;

    @Autowired
    private AuthAccountRepository authAccountRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearData() {
        userRepository.deleteAll();
        authAccountRepository.deleteAll();
    }

    @Test
    void registerUserCreatesAuthAndProfile() {
        AccountRegisterRequest request = new AccountRegisterRequest();
        request.setAccount("test_user");
        request.setPassword("password123");
        request.setRole("user");

        AccountSummary summary = accountAuthService.register(request);

        assertThat(summary.getAccount()).isEqualTo("test_user");
        assertThat(authAccountRepository.findByAccount("test_user")).isPresent();
        assertThat(userRepository.findByUsername("test_user")).isPresent();
    }
}
