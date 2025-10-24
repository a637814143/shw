package com.example.housekeeping.service;

import com.example.housekeeping.dto.CompanyServicePageResponse;
import com.example.housekeeping.dto.HousekeepServiceRequest;
import com.example.housekeeping.dto.HousekeepServiceResponse;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.enums.StaffStatus;
import com.example.housekeeping.repository.CompanyStaffRepository;
import com.example.housekeeping.repository.HousekeepServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.math.RoundingMode;

/**
 * 家政服务管理逻辑。
 */
@Service
public class HousekeepServiceManager {

    @Autowired
    private HousekeepServiceRepository housekeepServiceRepository;

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private CompanyStaffRepository companyStaffRepository;

    @Transactional(readOnly = true)
    public List<HousekeepServiceResponse> listAllServices(String keyword) {
        String normalizedKeyword = normalizeKeyword(keyword);
        List<HousekeepService> services = normalizedKeyword == null
            ? housekeepServiceRepository.findAll()
            : housekeepServiceRepository.searchByKeyword(normalizedKeyword);
        return services.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyServicePageResponse listForCurrentCompany(String keyword, int page, int size) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);
        String normalizedKeyword = normalizeKeyword(keyword);
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(1, size);
        Pageable pageable = PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, "id"));

        Page<HousekeepService> pageResult = normalizedKeyword == null
            ? housekeepServiceRepository.findByCompany(company, pageable)
            : housekeepServiceRepository.searchByCompanyAndKeyword(company, normalizedKeyword, pageable);

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
        service.setName(request.getName().trim());
        service.setUnit(request.getUnit().trim());
        service.setPrice(request.getPrice());
        service.setContact(request.getContact().trim());
        service.setDescription(normalizeDescription(request.getDescription()));
        service.setServiceTime(normalizeServiceTime(request.getServiceTime()));

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
        service.setName(request.getName().trim());
        service.setUnit(request.getUnit().trim());
        service.setPrice(request.getPrice());
        service.setContact(request.getContact().trim());
        service.setDescription(normalizeDescription(request.getDescription()));
        service.setServiceTime(normalizeServiceTime(request.getServiceTime()));
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
        int availableStaff = (int) companyStaffRepository.countByCompanyAndStatus(
            service.getCompany(),
            StaffStatus.IDLE
        );
        return new HousekeepServiceResponse(
            service.getId(),
            service.getName(),
            service.getUnit(),
            service.getPrice(),
            service.getContact(),
            service.getDescription(),
            service.getCompany().getId(),
            service.getCompany().getUsername(),
            service.getServiceTime(),
            availableStaff
        );
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeServiceTime(String serviceTime) {
        if (serviceTime == null) {
            return "";
        }
        return serviceTime.trim();
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String trimmed = keyword.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
