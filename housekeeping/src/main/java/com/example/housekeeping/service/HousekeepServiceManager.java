package com.example.housekeeping.service;

import com.example.housekeeping.dto.HousekeepServiceRequest;
import com.example.housekeeping.dto.HousekeepServiceResponse;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.HousekeepServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 家政服务管理逻辑。
 */
@Service
public class HousekeepServiceManager {

    @Autowired
    private HousekeepServiceRepository housekeepServiceRepository;

    @Autowired
    private AccountLookupService accountLookupService;

    @Transactional(readOnly = true)
    public List<HousekeepServiceResponse> listAllServices() {
        return housekeepServiceRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HousekeepServiceResponse> listForCurrentCompany() {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);
        return housekeepServiceRepository.findByCompany(company).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
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

    private void ensureRole(UserAll account, AccountRole expectedRole) {
        if (!expectedRole.getLabel().equals(account.getUserType())) {
            throw new RuntimeException("权限不足");
        }
    }

    private HousekeepServiceResponse mapToResponse(HousekeepService service) {
        return new HousekeepServiceResponse(
            service.getId(),
            service.getName(),
            service.getUnit(),
            service.getPrice(),
            service.getContact(),
            service.getDescription(),
            service.getCompany().getId(),
            service.getCompany().getUsername()
        );
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
