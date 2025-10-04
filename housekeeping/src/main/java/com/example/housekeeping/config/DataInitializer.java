package com.example.housekeeping.config;

import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.model.UserAccount;
import com.example.housekeeping.repository.ServiceItemRepository;
import com.example.housekeeping.repository.UserAccountRepository;
import com.example.housekeeping.service.AuthService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final ServiceItemRepository serviceItemRepository;
    private final UserAccountRepository userAccountRepository;
    private final AuthService authService;

    public DataInitializer(ServiceItemRepository serviceItemRepository,
                           UserAccountRepository userAccountRepository,
                           AuthService authService) {
        this.serviceItemRepository = serviceItemRepository;
        this.userAccountRepository = userAccountRepository;
        this.authService = authService;
    }

    @PostConstruct
    public void seed() {
        seedServiceItems();
        seedAccounts();
    }

    private void seedServiceItems() {
        if (serviceItemRepository.count() > 0) {
            return;
        }

        List<ServiceItem> defaults = List.of(
                new ServiceItem("家庭保洁", "2小时标准家庭保洁，包含地面、桌面和卫生间清洁。", 128),
                new ServiceItem("深度保洁", "针对厨房、卫生间的深度清洁服务，耗时约4小时。", 258),
                new ServiceItem("家电清洗", "空调、油烟机等常见家电拆洗服务。", 198)
        );

        serviceItemRepository.saveAll(defaults);
        log.info("已初始化默认家政服务项目: {} 项", defaults.size());
    }

    private void seedAccounts() {
        createAccountIfMissing("admin", "ADMIN", BigDecimal.ZERO, "Admin@123");
        createAccountIfMissing("demo_user", "USER", BigDecimal.valueOf(1000), "User@123");
        createAccountIfMissing("demo_provider", "PROVIDER", BigDecimal.ZERO, "Provider@123");
    }

    private void createAccountIfMissing(String username, String type, BigDecimal money, String rawPassword) {
        if (userAccountRepository.existsByUserName(username)) {
            return;
        }
        String encoded = authService.encodePassword(rawPassword);
        UserAccount account = new UserAccount(username, encoded, type, money);
        userAccountRepository.save(account);
        log.info("已初始化默认账号: {} ({})", username, type);
    }
}
