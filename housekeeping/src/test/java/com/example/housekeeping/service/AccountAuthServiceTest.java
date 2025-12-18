package com.example.housekeeping.service;

import com.example.housekeeping.dto.AccountRegisterRequest;
import com.example.housekeeping.dto.AccountSummary;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.repository.UserAllRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AccountAuthServiceTest {

    @Autowired
    private AccountAuthService accountAuthService;

    @Autowired
    private UserAllRepository userAllRepository;

    @BeforeEach
    void clearData() {
        userAllRepository.deleteAll();
    }

    @Test
    void registerUserCreatesRecordWithDefaultMoney() {
        AccountRegisterRequest request = new AccountRegisterRequest();
        request.setAccount("test_user");
        request.setPassword("password123");
        request.setRole("user");

        AccountSummary summary = accountAuthService.register(request);

        assertThat(summary.getAccount()).isEqualTo("test_user");
        UserAll saved = userAllRepository.findByUsername("test_user").orElseThrow();
        assertThat(saved.getMoney()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(saved.getUserType()).isEqualTo("普通用户");
        assertThat(saved.getPasswd()).isNotBlank();
    }
}
