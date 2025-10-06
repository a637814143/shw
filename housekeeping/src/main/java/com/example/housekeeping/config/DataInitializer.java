package com.example.housekeeping.config;

import com.example.housekeeping.domain.entity.Admin;
import com.example.housekeeping.domain.entity.CarouselItem;
import com.example.housekeeping.domain.entity.HomeTip;
import com.example.housekeeping.domain.entity.HousekeepingService;
import com.example.housekeeping.domain.entity.ServiceCategory;
import com.example.housekeeping.domain.entity.ServiceProvider;
import com.example.housekeeping.domain.entity.SystemAnnouncement;
import com.example.housekeeping.domain.entity.UserAccount;
import com.example.housekeeping.domain.enums.AnnouncementTarget;
import com.example.housekeeping.repository.AdminRepository;
import com.example.housekeeping.security.PasswordHasher;
import com.example.housekeeping.repository.CarouselItemRepository;
import com.example.housekeeping.repository.HomeTipRepository;
import com.example.housekeeping.repository.HousekeepingServiceRepository;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import com.example.housekeeping.repository.ServiceProviderRepository;
import com.example.housekeeping.repository.SystemAnnouncementRepository;
import com.example.housekeeping.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!prod")
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final UserAccountRepository userAccountRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final HousekeepingServiceRepository housekeepingServiceRepository;
    private final SystemAnnouncementRepository systemAnnouncementRepository;
    private final HomeTipRepository homeTipRepository;
    private final CarouselItemRepository carouselItemRepository;
    private final PasswordHasher passwordHasher;

    public DataInitializer(AdminRepository adminRepository,
            UserAccountRepository userAccountRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServiceCategoryRepository serviceCategoryRepository,
            HousekeepingServiceRepository housekeepingServiceRepository,
            SystemAnnouncementRepository systemAnnouncementRepository,
            HomeTipRepository homeTipRepository,
            CarouselItemRepository carouselItemRepository,
            PasswordHasher passwordHasher) {
        this.adminRepository = adminRepository;
        this.userAccountRepository = userAccountRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.housekeepingServiceRepository = housekeepingServiceRepository;
        this.systemAnnouncementRepository = systemAnnouncementRepository;
        this.homeTipRepository = homeTipRepository;
        this.carouselItemRepository = carouselItemRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(String... args) {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordHasher.hash("admin123"));
            admin.setFullName("系统管理员");
            admin.setEnabled(true);
            admin.setLastLoginAt(LocalDateTime.now());
            adminRepository.save(admin);
        }

        if (userAccountRepository.count() == 0) {
            UserAccount user = new UserAccount();
            user.setUsername("user");
            user.setPassword(passwordHasher.hash("123456"));
            user.setFullName("测试用户");
            user.setEmail("user@example.com");
            user.setPhone("13800000000");
            user.setBalance(new BigDecimal("200"));
            userAccountRepository.save(user);
        }

        if (serviceProviderRepository.count() == 0) {
            ServiceProvider provider = new ServiceProvider();
            provider.setUsername("provider");
            provider.setPassword(passwordHasher.hash("provider123"));
            provider.setFullName("金牌保洁员");
            provider.setPhone("13900000000");
            provider.setServiceArea("上海市");
            provider.setSkills("深度保洁, 家电清洗");
            provider.setCertified(true);
            provider.setBiography("拥有5年家政服务经验，擅长厨房和卫生间深度清洁");
            provider.setCompletedOrders(86);
            provider.setBalance(new BigDecimal("520"));
            serviceProviderRepository.save(provider);
        }

        if (serviceCategoryRepository.count() == 0) {
            ServiceCategory cleaning = new ServiceCategory();
            cleaning.setName("家庭保洁");
            cleaning.setDescription("日常家庭清洁服务");
            cleaning.setSortOrder(1);
            serviceCategoryRepository.save(cleaning);

            ServiceCategory appliance = new ServiceCategory();
            appliance.setName("家电清洗");
            appliance.setDescription("专业家电清洗服务");
            appliance.setSortOrder(2);
            serviceCategoryRepository.save(appliance);

            List<HousekeepingService> services = List.of(
                    createService(cleaning, "日常保洁", new BigDecimal("128"), "2小时日常保洁，提供基础工具"),
                    createService(cleaning, "开荒保洁", new BigDecimal("398"), "新居开荒保洁，包含厨房、卫生间深度清洁"),
                    createService(appliance, "空调清洗", new BigDecimal("168"), "挂机/柜机专业拆洗服务")
            );
            housekeepingServiceRepository.saveAll(services);
        }

        if (systemAnnouncementRepository.count() == 0) {
            SystemAnnouncement announcement = new SystemAnnouncement();
            announcement.setTitle("欢迎使用家政服务管理平台");
            announcement.setContent("平台现已上线，提供便捷的家政预约、服务管理和数据统计功能。");
            announcement.setTarget(AnnouncementTarget.ALL);
            announcement.setPublishedAt(LocalDateTime.now());
            announcement.setPublishedBy("系统管理员");
            announcement.setEnabled(true);
            announcement.setPinned(true);
            systemAnnouncementRepository.save(announcement);
        }

        if (homeTipRepository.count() == 0) {
            HomeTip tip = new HomeTip();
            tip.setTitle("厨房清洁小窍门");
            tip.setSummary("三步轻松搞定厨房油污");
            tip.setContent("使用小苏打加白醋可以快速分解油污，喷洒后静置5分钟再擦拭即可。");
            tip.setFeatured(true);
            homeTipRepository.save(tip);
        }

        if (carouselItemRepository.count() == 0) {
            housekeepingServiceRepository.findAll().stream().findFirst().ifPresent(service -> {
                CarouselItem item = new CarouselItem();
                item.setTitle("热销服务推荐");
                item.setImageUrl("https://placehold.co/1200x400?text=Housekeeping");
                item.setSortOrder(1);
                item.setEnabled(true);
                item.setService(service);
                carouselItemRepository.save(item);
            });
        }
    }

    private HousekeepingService createService(ServiceCategory category, String title, BigDecimal price, String description) {
        HousekeepingService service = new HousekeepingService();
        service.setCategory(category);
        service.setTitle(title);
        service.setPrice(price);
        service.setDescription(description);
        service.setFeatured(true);
        service.setCoverImageUrl("https://placehold.co/400x300");
        service.setOrderCount(10);
        return service;
    }
}
