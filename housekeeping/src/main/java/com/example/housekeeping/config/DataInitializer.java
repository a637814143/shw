package com.example.housekeeping.config;

import com.example.housekeeping.model.AccountType;
import com.example.housekeeping.model.BookingStatus;
import com.example.housekeeping.model.ServiceBooking;
import com.example.housekeeping.model.ServiceItem;
import com.example.housekeeping.model.UserAccount;
import com.example.housekeeping.repository.ServiceBookingRepository;
import com.example.housekeeping.repository.ServiceItemRepository;
import com.example.housekeeping.repository.UserAccountRepository;
import com.example.housekeeping.service.AuthService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserAccountRepository userAccountRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final AuthService authService;

    public DataInitializer(UserAccountRepository userAccountRepository,
                           ServiceItemRepository serviceItemRepository,
                           ServiceBookingRepository serviceBookingRepository,
                           AuthService authService) {
        this.userAccountRepository = userAccountRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.serviceBookingRepository = serviceBookingRepository;
        this.authService = authService;
    }

    @PostConstruct
    public void init() {
        initAccounts();
        initServices();
        initBookings();
    }

    private void initAccounts() {
        createAccountIfMissing("admin", "Admin@123", AccountType.ADMIN, BigDecimal.ZERO);
        createAccountIfMissing("demo_user", "User@123", AccountType.USER, BigDecimal.valueOf(1500));
        createAccountIfMissing("demo_provider", "Provider@123", AccountType.PROVIDER, BigDecimal.ZERO);
    }

    private void createAccountIfMissing(String username, String password, AccountType type, BigDecimal balance) {
        userAccountRepository.findByUserNameIgnoreCase(username).ifPresentOrElse(account -> {
            if (account.getMoney().compareTo(balance) != 0) {
                account.setMoney(balance);
                userAccountRepository.save(account);
            }
        }, () -> {
            UserAccount account = new UserAccount(username, authService.encodePassword(password), type.name(), balance);
            account.setTypes(type);
            account.setMoney(balance);
            userAccountRepository.save(account);
            log.info("初始化账号: {}", username);
        });
    }

    private void initServices() {
        if (serviceItemRepository.count() > 0) {
            return;
        }

        List<ServiceItem> items = List.of(
                new ServiceItem("家庭日常保洁", "2小时基础保洁，覆盖客厅、卧室与卫生间。", BigDecimal.valueOf(128)),
                new ServiceItem("深度厨房清洁", "针对油污重灾区的厨房提供除油、消毒一体化服务。", BigDecimal.valueOf(258)),
                new ServiceItem("全屋开荒保洁", "适合新房入住前的全面清洁，含窗户与地面打蜡。", BigDecimal.valueOf(398)),
                new ServiceItem("家电专业清洗", "空调、油烟机、洗衣机等家电专业拆洗与杀菌。", BigDecimal.valueOf(198))
        );
        serviceItemRepository.saveAll(items);
        log.info("初始化服务项目 {} 个", items.size());
    }

    private void initBookings() {
        if (serviceBookingRepository.count() > 0) {
            return;
        }

        ServiceItem defaultItem = serviceItemRepository.findByNameIgnoreCase("家庭日常保洁")
                .orElseGet(() -> serviceItemRepository.findAll().stream().findFirst().orElse(null));

        if (defaultItem == null) {
            return;
        }

        ServiceBooking booking = new ServiceBooking();
        booking.setCustomerName("张女士");
        booking.setPhone("13800001234");
        booking.setAddress("上海市浦东新区花木路88号");
        booking.setServiceDate(LocalDate.now().plusDays(1));
        booking.setNotes("上午9点前到达，门禁8888。");
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedBy("demo_user");
        booking.setServiceItem(defaultItem);
        booking.setPrice(defaultItem.getPrice());

        serviceBookingRepository.save(booking);
        log.info("初始化示例预约数据");
    }
}

