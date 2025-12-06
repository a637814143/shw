package com.example.housekeeping.service;

import com.example.housekeeping.dto.CompanyServicePageResponse;
import com.example.housekeeping.dto.HousekeepServiceRequest;
import com.example.housekeeping.dto.HousekeepServiceResponse;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.enums.HousekeepServiceStatus;
import com.example.housekeeping.repository.HousekeepServiceRepository;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.math.RoundingMode;

/**
 * 家政服务管理逻辑。
 */
@Service
public class HousekeepServiceManager {

    private static final String DEFAULT_SERVICE_TIME = "2小时";
    private static final HousekeepServiceStatus DEFAULT_STATUS = HousekeepServiceStatus.PENDING;

    @Autowired
    private HousekeepServiceRepository housekeepServiceRepository;

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private CompanyStaffService companyStaffService;

    @Transactional(readOnly = true)
    public List<HousekeepServiceResponse> listAllServices(String keyword, Long categoryId) {
        String normalizedKeyword = normalizeKeyword(keyword);
        ServiceCategory category = categoryId == null ? null : resolveCategory(categoryId);
        List<HousekeepService> services = normalizedKeyword == null
            ? housekeepServiceRepository.findAll()
            : housekeepServiceRepository.searchByKeyword(normalizedKeyword);
        if (category != null) {
            services = services.stream()
                .filter(service -> service.getCategory() != null
                    && Objects.equals(service.getCategory().getId(), category.getId()))
                .collect(Collectors.toList());
        }
        return services.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyServicePageResponse listForCurrentCompany(String keyword, Long categoryId, int page, int size) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);
        String normalizedKeyword = normalizeKeyword(keyword);
        ServiceCategory categoryFilter = findCategoryForFilter(categoryId);
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(1, size);
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<HousekeepService> pageResult = housekeepServiceRepository.searchByCompanyWithFilters(
            company,
            categoryFilter,
            normalizedKeyword,
            pageable
        );

        List<HousekeepServiceResponse> items = pageResult.getContent().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());

        Double averagePrice = housekeepServiceRepository.findAveragePriceByCompany(company);
        BigDecimal average = averagePrice == null
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(averagePrice).setScale(2, RoundingMode.HALF_UP);

        return new CompanyServicePageResponse(
            items,
            pageResult.getTotalElements(),
            safePage,
            safeSize,
            average
        );
    }

    @Transactional
    public HousekeepServiceResponse createService(HousekeepServiceRequest request) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);

        HousekeepService service = new HousekeepService();
        service.setCompany(company);
        service.setCategory(resolveCategory(request.getCategoryId()));
        service.setName(request.getName().trim());
        service.setUnit(request.getUnit().trim());
        service.setPrice(request.getPrice());
        service.setContact(request.getContact().trim());
        service.setServiceTime(DEFAULT_SERVICE_TIME);
        service.setStatus(DEFAULT_STATUS);
        service.setDescription(normalizeDescription(request.getDescription()));

        return mapToResponse(housekeepServiceRepository.save(service));
    }

    @Transactional
    public HousekeepServiceResponse updateService(Long id, HousekeepServiceRequest request) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);

        HousekeepService service = housekeepServiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        if (!service.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("无权操作其他公司服务");
        }
        ServiceCategory category = resolveCategory(request.getCategoryId());
        service.setCategory(category);
        service.setName(request.getName().trim());
        service.setUnit(request.getUnit().trim());
        service.setPrice(request.getPrice());
        service.setContact(request.getContact().trim());
        service.setServiceTime(DEFAULT_SERVICE_TIME);
        service.setStatus(DEFAULT_STATUS);
        service.setDescription(normalizeDescription(request.getDescription()));
        return mapToResponse(housekeepServiceRepository.save(service));
    }

    @Transactional(readOnly = true)
    public HousekeepService getServiceById(Long id) {
        if (id == null) {
            throw new RuntimeException("请选择服务");
        }
        return housekeepServiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
    }

    @Transactional(readOnly = true)
    public List<HousekeepServiceResponse> listForAdmin(String keyword, Long categoryId, HousekeepServiceStatus status) {
        String normalizedKeyword = normalizeKeyword(keyword);
        ServiceCategory category = categoryId == null ? null : resolveCategory(categoryId);

        List<HousekeepService> services = housekeepServiceRepository.searchForAdmin(category, normalizedKeyword, status);

        return services.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public HousekeepServiceResponse reviewService(Long serviceId, boolean approve, String reason) {
        HousekeepService service = housekeepServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("服务不存在"));

        service.setStatus(approve ? HousekeepServiceStatus.APPROVED : HousekeepServiceStatus.REJECTED);
        return mapToResponse(housekeepServiceRepository.save(service));
    }

    @Transactional
    public void deleteService(Long id) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);

        HousekeepService service = housekeepServiceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        if (!service.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("无权操作其他公司服务");
        }
        housekeepServiceRepository.delete(service);
    }

    @Transactional
    public void deleteServices(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);

        List<Long> distinctIds = ids.stream()
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());
        if (distinctIds.isEmpty()) {
            return;
        }

        List<HousekeepService> services = housekeepServiceRepository.findAllById(distinctIds);
        if (services.size() != distinctIds.size()) {
            throw new RuntimeException("部分服务不存在或已被删除");
        }

        for (HousekeepService service : services) {
            if (!service.getCompany().getId().equals(company.getId())) {
                throw new RuntimeException("无权操作其他公司服务");
            }
        }

        housekeepServiceRepository.deleteAll(services);
    }

    private void ensureRole(UserAll account, AccountRole expectedRole) {
        if (!expectedRole.getLabel().equals(account.getUserType())) {
            throw new RuntimeException("权限不足");
        }
    }

    private HousekeepServiceResponse mapToResponse(HousekeepService service) {
        ServiceCategory category = service.getCategory();
        return new HousekeepServiceResponse(
            service.getId(),
            service.getName(),
            service.getUnit(),
            service.getPrice(),
            service.getContact(),
            formatServiceTime(service),
            service.getDescription(),
            service.getCompany().getId(),
            service.getCompany().getUsername(),
            category == null ? null : category.getId(),
            category == null ? null : category.getName(),
            availableStaffForService(service),
            service.getStatus() == null ? null : service.getStatus().name()
        );
    }

    private long availableStaffForService(HousekeepService service) {
        ServiceCategory category = service.getCategory();
        if (category == null) {
            return 0L;
        }
        return companyStaffService.countAvailableStaffForCompanyCategory(service.getCompany(), category);
    }

    private ServiceCategory findCategoryForFilter(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return serviceCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("服务分类不存在"));
    }

    private ServiceCategory resolveCategory(Long categoryId) {
        if (categoryId == null) {
            throw new RuntimeException("请选择服务分类");
        }
        return serviceCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("服务分类不存在"));
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeServiceTime(String serviceTime) {
        return DEFAULT_SERVICE_TIME;
    }

    private String formatServiceTime(HousekeepService service) {
        return normalizeServiceTime(service.getServiceTime());
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String trimmed = keyword.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
