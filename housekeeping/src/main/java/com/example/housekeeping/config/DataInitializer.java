package com.example.housekeeping.config;

import com.example.housekeeping.entity.AppointmentStatus;
import com.example.housekeeping.entity.CarouselItem;
import com.example.housekeeping.entity.FavoriteRecord;
import com.example.housekeeping.entity.HousekeepingServiceItem;
import com.example.housekeeping.entity.HousekeepingTip;
import com.example.housekeeping.entity.ProviderCertification;
import com.example.housekeeping.entity.ProviderCertificationStatus;
import com.example.housekeeping.entity.ServiceAppointment;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.entity.ServiceEvaluation;
import com.example.housekeeping.entity.SystemNotice;
import com.example.housekeeping.entity.UserForHousekeeper;
import com.example.housekeeping.entity.WithdrawalAccountType;
import com.example.housekeeping.entity.WithdrawalRecord;
import com.example.housekeeping.repository.CarouselItemRepository;
import com.example.housekeeping.repository.FavoriteRecordRepository;
import com.example.housekeeping.repository.HousekeepingServiceItemRepository;
import com.example.housekeeping.repository.HousekeepingTipRepository;
import com.example.housekeeping.repository.ProviderCertificationRepository;
import com.example.housekeeping.repository.ServiceAppointmentRepository;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import com.example.housekeeping.repository.ServiceEvaluationRepository;
import com.example.housekeeping.repository.SystemNoticeRepository;
import com.example.housekeeping.repository.UserForHousekeeperRepository;
import com.example.housekeeping.repository.WithdrawalRecordRepository;
import com.example.housekeeping.util.PasswordTransformer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserForHousekeeperRepository userRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final HousekeepingServiceItemRepository serviceRepository;
    private final ServiceAppointmentRepository appointmentRepository;
    private final WithdrawalRecordRepository withdrawalRepository;
    private final CarouselItemRepository carouselRepository;
    private final SystemNoticeRepository noticeRepository;
    private final ServiceEvaluationRepository evaluationRepository;
    private final HousekeepingTipRepository tipRepository;
    private final FavoriteRecordRepository favoriteRepository;
    private final ProviderCertificationRepository certificationRepository;

    public DataInitializer(UserForHousekeeperRepository userRepository,
                           ServiceCategoryRepository categoryRepository,
                           HousekeepingServiceItemRepository serviceRepository,
                           ServiceAppointmentRepository appointmentRepository,
                           WithdrawalRecordRepository withdrawalRepository,
                           CarouselItemRepository carouselRepository,
                           SystemNoticeRepository noticeRepository,
                           ServiceEvaluationRepository evaluationRepository,
                           HousekeepingTipRepository tipRepository,
                           FavoriteRecordRepository favoriteRepository,
                           ProviderCertificationRepository certificationRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.serviceRepository = serviceRepository;
        this.appointmentRepository = appointmentRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.carouselRepository = carouselRepository;
        this.noticeRepository = noticeRepository;
        this.evaluationRepository = evaluationRepository;
        this.tipRepository = tipRepository;
        this.favoriteRepository = favoriteRepository;
        this.certificationRepository = certificationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        initUsers();
        Map<String, ServiceCategory> categories = initCategories();
        initServices(categories);
        initAppointments();
        initWithdrawals();
        initCarousels();
        initNotices();
        initEvaluations();
        initTips();
        initFavorites();
        initProviderCertifications();
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            UserForHousekeeper admin = new UserForHousekeeper();
            admin.setUsername("admin");
            admin.setPassword(PasswordTransformer.encode("admin123"));
            admin.setUsertype("admin");
            admin.setUsermoney(BigDecimal.valueOf(1000));

            UserForHousekeeper provider = new UserForHousekeeper();
            provider.setUsername("provider");
            provider.setPassword(PasswordTransformer.encode("provider123"));
            provider.setUsertype("provider");
            provider.setUsermoney(BigDecimal.valueOf(500));

            UserForHousekeeper user = new UserForHousekeeper();
            user.setUsername("user");
            user.setPassword(PasswordTransformer.encode("123456"));
            user.setUsertype("user");
            user.setUsermoney(BigDecimal.valueOf(200));

            userRepository.saveAll(List.of(admin, provider, user));
        }
    }

    private Map<String, ServiceCategory> initCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.saveAll(List.of(
                    ServiceCategory.builder().name("日常保洁").description("专业工具，掸灰扫拖").icon("🧹").build(),
                    ServiceCategory.builder().name("擦玻璃").description("专业药剂，洁净玻璃").icon("🪟").build(),
                    ServiceCategory.builder().name("深度保洁").description("明星服务，洁净超乎想象").icon("🧼").build(),
                    ServiceCategory.builder().name("新居开荒").description("新房入住开荒，清扫装修痕迹").icon("🏠").build(),
                    ServiceCategory.builder().name("厨房保洁").description("厨房保养，感受清爽空间").icon("🍳").build(),
                    ServiceCategory.builder().name("卫生间保洁").description("专业消毒，清新舒爽").icon("🚿").build(),
                    ServiceCategory.builder().name("家具保养").description("专业护理，延长使用寿命").icon("🛋️").build(),
                    ServiceCategory.builder().name("地面清洁").description("多种材质，专业处理").icon("🧹").build(),
                    ServiceCategory.builder().name("家电清洗").description("彻底清洁，健康使用").icon("📺").build()
            ));
        }
        Map<String, ServiceCategory> categories = new HashMap<>();
        categoryRepository.findAll().forEach(category -> categories.put(category.getName(), category));
        return categories;
    }

    private void initServices(Map<String, ServiceCategory> categories) {
        if (serviceRepository.count() == 0) {
            serviceRepository.saveAll(List.of(
                    service("沙发保养清洗【包含皮革养护】", 50, "次", 2, 16,
                            "皮沙发保养", "https://picsum.photos/seed/service1/100/100", categories),
                    service("地板打檫10平米", 66, "10平米", 4, 11,
                            "地面清洁", "https://picsum.photos/seed/service2/100/100", categories),
                    service("全屋清洁【大扫除】", 399, "次", 0, 1,
                            "深度保洁", "https://picsum.photos/seed/service3/100/100", categories),
                    service("卫生间保养清洁【深度清洁】", 88, "次", 0, 0,
                            "卫生间保洁", "https://picsum.photos/seed/service4/100/100", categories),
                    service("厨房保养清洁【深度清洁】", 338, "次", 0, 0,
                            "厨房保洁", "https://picsum.photos/seed/service5/100/100", categories),
                    service("新房开荒打扫平米【包运送垃圾/包验收】", 9.9, "平米", 12, 24,
                            "新居开荒", "https://picsum.photos/seed/service6/100/100", categories),
                    service("全屋家电清洗套餐", 299, "次", 3, 6,
                            "家电清洗", "https://picsum.photos/seed/service7/100/100", categories),
                    service("家庭长期保洁月度服务", 1299, "月", 5, 8,
                            "家庭长期保洁", "https://picsum.photos/seed/service8/100/100", categories)
            ));
        }
    }

    private HousekeepingServiceItem service(String name, double price, String unit, int sales, int popularity,
                                            String categoryName, String imageUrl, Map<String, ServiceCategory> categories) {
        return HousekeepingServiceItem.builder()
                .name(name)
                .price(BigDecimal.valueOf(price))
                .unit(unit)
                .sales(sales)
                .popularity(popularity)
                .description(name)
                .imageUrl(imageUrl)
                .category(categories.getOrDefault(categoryName, categories.values().iterator().next()))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void initAppointments() {
        if (appointmentRepository.count() == 0) {
            appointmentRepository.saveAll(List.of(
                    appointment("新房开荒打扫平米【包运送垃圾/包验收】", 1, 9.9, "小张", "家政小丁",
                            "13989997888", "枫叶小区12栋301", "13989997888",
                            AppointmentStatus.COMPLETED, true,
                            "2025-05-17 10:00:00", "2025-05-17 22:31:29", "2025-05-17 22:31:18"),
                    appointment("烟机打檫10平米", 1, 66, "小张", "家政小冯",
                            "13989997888", "枫叶小区12栋301", "13989997888",
                            AppointmentStatus.COMPLETED, true,
                            "2025-05-17 10:00:00", "2025-05-17 22:28:56", "2025-05-17 20:09:20"),
                    appointment("沙发保养清洗【包含皮革养护】", 1, 50, "nnn", "家政小冯",
                            "13989997888", "枫叶小区12栋301", "13989998999",
                            AppointmentStatus.COMPLETED, true,
                            "2025-05-16 10:00:00", "2025-05-16 20:55:05", "2025-05-16 08:05:57"),
                    appointment("沙发保养清洗【包含皮革养护】", 1, 50, "nnn", null,
                            "13989998999", null, null,
                            AppointmentStatus.PENDING, false,
                            "2025-05-17 10:00:00", "2025-05-17 20:55:05", "2025-05-16 08:05:56"),
                    appointment("地板打檫10平米", 1, 66, "小张", "家政小敏",
                            "13988997788", "枫叶社区...", "13988997788",
                            AppointmentStatus.COMPLETED, true,
                            "2025-05-09 10:00:00", "2025-05-09 12:00:00", "2025-05-08 10:00:00")
            ));
        }
    }

    private ServiceAppointment appointment(String serviceName, int quantity, double totalAmount, String userName,
                                           String providerName, String contactPhone, String contactAddress,
                                           String servicePhone, AppointmentStatus status, boolean assigned,
                                           String startTime, String endTime, String appointmentTime) {
        return ServiceAppointment.builder()
                .serviceName(serviceName)
                .quantity(quantity)
                .totalAmount(BigDecimal.valueOf(totalAmount))
                .userName(userName)
                .providerName(providerName)
                .contactPhone(contactPhone)
                .contactAddress(contactAddress)
                .servicePhone(servicePhone)
                .status(status)
                .statusText(statusText(status))
                .startTime(parse(startTime))
                .endTime(parse(endTime))
                .appointmentTime(parse(appointmentTime))
                .assigned(assigned)
                .build();
    }

    private String statusText(AppointmentStatus status) {
        return switch (status) {
            case PENDING -> "已预约";
            case PROCESSING -> "处理中";
            case COMPLETED -> "已完成";
            case CANCELLED -> "已取消";
        };
    }

    private void initWithdrawals() {
        if (withdrawalRepository.count() == 0) {
            withdrawalRepository.saveAll(List.of(
                    withdrawal(100, WithdrawalAccountType.WECHAT, "13989997788", "家政小丁", "2025-05-23 09:56:26"),
                    withdrawal(1, WithdrawalAccountType.ALIPAY, "13990998777", "家政小月", "2025-05-16 09:04:47"),
                    withdrawal(100, WithdrawalAccountType.ALIPAY, "13877956666", "家政小黄", "2025-05-13 13:31:07"),
                    withdrawal(100, WithdrawalAccountType.WECHAT, "13988776688", "家政小黄", "2025-05-12 18:28:58")
            ));
        }
    }

    private WithdrawalRecord withdrawal(double amount, WithdrawalAccountType type, String accountNumber,
                                        String providerName, String time) {
        return WithdrawalRecord.builder()
                .amount(BigDecimal.valueOf(amount))
                .accountType(type)
                .accountNumber(accountNumber)
                .providerName(providerName)
                .withdrawalTime(parse(time))
                .build();
    }

    private void initCarousels() {
        if (carouselRepository.count() == 0) {
            carouselRepository.saveAll(List.of(
                    CarouselItem.builder().imageUrl("https://picsum.photos/seed/service1/300/150").serviceName("全屋清洁 [大扫除]").build(),
                    CarouselItem.builder().imageUrl("https://picsum.photos/seed/service2/300/150").serviceName("2小时全屋日常保洁 [公司福利] 快速上门").build()
            ));
        }
    }

    private void initNotices() {
        if (noticeRepository.count() == 0) {
            noticeRepository.saveAll(List.of(
                    notice("深度保洁特惠", "即日起，本家政推出深度保洁特惠！专业团队环绕环保清洁剂，彻底清除顽固污渍，让您的家焕然一新！"),
                    notice("家电清洗福利", "家电久未清洗易藏污垢！现开展家电清洗福利，空调、油烟机、洗衣机等多种家电一站式清洁，价格优惠！"),
                    notice("新房开荒保洁特惠", "新房开荒不用愁！专业团队采用环保药剂，高效去除装修残留物，让您轻松入住新家！")
            ));
        }
    }

    private SystemNotice notice(String title, String content) {
        return SystemNotice.builder()
                .title(title)
                .content(content)
                .publishTime(parse("2025-05-11 15:51:17"))
                .build();
    }

    private void initEvaluations() {
        if (evaluationRepository.count() == 0) {
            evaluationRepository.saveAll(List.of(
                    evaluation("家政小丁", "新房开荒打扫平米【包运送垃圾/包验收】", "小张", 4.8, "服务非常周到，卫生打扫得很仔细！", "2025-05-23 09:56:26"),
                    evaluation("家政小冯", "烟机打檫10平米", "小张", 4.5, "清洁的很好，速度也很快。", "2025-05-17 22:28:56"),
                    evaluation("家政小冯", "沙发保养清洗【包含皮革养护】", "nnn", 4.7, "沙发焕然一新，满意！", "2025-05-16 20:55:05"),
                    evaluation("家政小丁", "4小时全屋日常保洁【中户型推荐】快速上门", "小张", 4.5, "非常好！", "2025-05-13 09:09:00"),
                    evaluation("家政小丁", "4小时全屋日常保洁【中户型推荐】快速上门", "小张", 3.5, "非常好", "2025-05-09 16:45:17")
            ));
        }
    }

    private ServiceEvaluation evaluation(String providerName, String serviceName, String userName,
                                         double rating, String content, String time) {
        return ServiceEvaluation.builder()
                .providerName(providerName)
                .serviceName(serviceName)
                .userName(userName)
                .rating(BigDecimal.valueOf(rating))
                .content(content)
                .evaluationTime(parse(time))
                .build();
    }

    private void initTips() {
        if (tipRepository.count() == 0) {
            tipRepository.saveAll(List.of(
                    tip("让生活空间焕然一新", "厨房是家中油污的\"重灾区\"", "厨房是家中油污的\"重灾区\"，尤其是抽油烟机和灶台周围...", "https://via.placeholder.com/120x80/ff6b6b/ffffff?text=清洁", 34, "2025-05-01 15:56:24"),
                    tip("打造井井有条的家", "衣物收纳要根据季节和使用频率进行分类", "衣物收纳要根据季节和使用频率进行分类。当季常穿的衣物放在容易取放的位置...", "https://via.placeholder.com/120x80/4ecdc4/ffffff?text=收纳", 7, "2025-05-02 15:56:24"),
                    tip("守护家人的平安", "家中的电器使用频繁，用电安全不容忽视", "家中的电器使用频繁，用电安全不容忽视。首先，要选择质量合格的电器产品...", "https://via.placeholder.com/120x80/45b7d1/ffffff?text=安全", 2, "2025-05-03 15:56:24"),
                    tip("卫生间深度清洁指南", "让卫生间作为家中使用频率最高的区域之一", "卫生间作为家中使用频率最高的区域之一，容易滋生细菌和产生异味...", "https://via.placeholder.com/120x80/96ceb4/ffffff?text=卫生间", 2, "2025-05-04 15:56:24"),
                    tip("告别油烟，焕新美味空间", "厨房是美食诞生的地方，却也是油烟的集中地", "厨房是美食诞生的地方，却也是油烟的集中地。为了保持厨房空气清新...", "https://via.placeholder.com/120x80/f7b733/ffffff?text=厨房", 0, "2025-05-05 15:56:24"),
                    tip("让阳台成为舒适的休闲空间", "阳台不仅是晾晒衣物的地方", "阳台不仅是晾晒衣物的地方，还可以打造成舒适的休闲空间...", "https://via.placeholder.com/120x80/45ada8/ffffff?text=阳台", 5, "2025-05-06 15:56:24")
            ));
        }
    }

    private HousekeepingTip tip(String title, String intro, String content, String imageUrl, int views, String time) {
        return HousekeepingTip.builder()
                .title(title)
                .intro(intro)
                .content(content)
                .imageUrl(imageUrl)
                .publishTime(parse(time))
                .views(views)
                .build();
    }

    private void initFavorites() {
        if (favoriteRepository.count() == 0) {
            favoriteRepository.saveAll(List.of(
                    favorite("全屋清洁 [大扫除]", "小张", "2025-05-23 09:53:42"),
                    favorite("日常保洁 [深度清洁]", "小李", "2025-05-22 14:32:18"),
                    favorite("家电清洗 [空调]", "小王", "2025-05-21 10:15:57")
            ));
        }
    }

    private FavoriteRecord favorite(String serviceName, String userName, String time) {
        return FavoriteRecord.builder()
                .serviceName(serviceName)
                .userName(userName)
                .favoriteTime(parse(time))
                .build();
    }

    private void initProviderCertifications() {
        if (certificationRepository.count() == 0) {
            certificationRepository.saveAll(List.of(
                    certification("家政小敏", "https://picsum.photos/seed/cert1/100/100", "https://picsum.photos/seed/id1/100/100",
                            "34101019990906-4566", "13889877788", "合肥市枫叶社区...", ProviderCertificationStatus.PENDING,
                            "2025-05-20 09:00:00", null, null),
                    certification("家政小黄", "https://picsum.photos/seed/cert2/100/100", "https://picsum.photos/seed/id2/100/100",
                            "34101019910908-6576", "13888993377", "合肥市光大大道...", ProviderCertificationStatus.APPROVED,
                            "2025-05-18 10:00:00", "2025-05-19 15:00:00", "资料齐全，审核通过"),
                    certification("家政小月", "https://picsum.photos/seed/cert3/100/100", "https://picsum.photos/seed/id3/100/100",
                            "34101019931008-4564", "13889979966", "合肥市光大大道...", ProviderCertificationStatus.APPROVED,
                            "2025-05-17 11:00:00", "2025-05-18 14:30:00", "经验丰富，通过审核"),
                    certification("家政小丁", "https://picsum.photos/seed/cert4/100/100", "https://picsum.photos/seed/id4/100/100",
                            "34101019951008-5674", "13888997788", "合肥市光大大道...", ProviderCertificationStatus.APPROVED,
                            "2025-05-16 13:00:00", "2025-05-17 09:00:00", "已通过背景审查")
            ));
        }
    }

    private ProviderCertification certification(String name, String certificateUrl, String idCardUrl,
                                                String idCardNumber, String contact, String address,
                                                ProviderCertificationStatus status, String appliedAt,
                                                String reviewedAt, String comment) {
        return ProviderCertification.builder()
                .name(name)
                .certificateUrl(certificateUrl)
                .idCardUrl(idCardUrl)
                .idCardNumber(idCardNumber)
                .contact(contact)
                .address(address)
                .status(status)
                .reviewComment(comment)
                .appliedAt(parse(appliedAt))
                .reviewedAt(reviewedAt != null ? parse(reviewedAt) : null)
                .build();
    }

    private LocalDateTime parse(String text) {
        return LocalDateTime.parse(text, FORMATTER);
    }
}
