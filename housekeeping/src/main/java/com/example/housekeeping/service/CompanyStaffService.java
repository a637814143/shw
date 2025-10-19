package com.example.housekeeping.service;

import com.example.housekeeping.dto.CompanyStaffRequest;
import com.example.housekeeping.dto.CompanyStaffResponse;
import com.example.housekeeping.entity.CompanyStaff;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.CompanyStaffRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 家政公司人员管理。
 */
@Service
public class CompanyStaffService {

    @Autowired
    private CompanyStaffRepository companyStaffRepository;

    @Autowired
    private AccountLookupService accountLookupService;

    @Transactional
    public CompanyStaffResponse createStaff(CompanyStaffRequest request) {
        UserAll company = currentCompany();
        CompanyStaff staff = new CompanyStaff();
        staff.setCompany(company);
        staff.setStaffName(request.getStaffName().trim());
        staff.setStaffPhone(request.getStaffPhone().trim());
        staff.setRemarks(normalizeRemarks(request.getRemarks()));
        staff.setCreatedAt(Instant.now());
        staff.setUpdatedAt(staff.getCreatedAt());
        return mapToResponse(companyStaffRepository.save(staff));
    }

    @Transactional
    public CompanyStaffResponse updateStaff(Long staffId, CompanyStaffRequest request) {
        UserAll company = currentCompany();
        CompanyStaff staff = companyStaffRepository.findByIdAndCompany(staffId, company)
            .orElseThrow(() -> new RuntimeException("家政人员不存在"));
        staff.setStaffName(request.getStaffName().trim());
        staff.setStaffPhone(request.getStaffPhone().trim());
        staff.setRemarks(normalizeRemarks(request.getRemarks()));
        staff.setUpdatedAt(Instant.now());
        return mapToResponse(companyStaffRepository.save(staff));
    }

    @Transactional
    public void deleteStaff(Long staffId) {
        UserAll company = currentCompany();
        CompanyStaff staff = companyStaffRepository.findByIdAndCompany(staffId, company)
            .orElseThrow(() -> new RuntimeException("家政人员不存在"));
        companyStaffRepository.delete(staff);
    }

    @Transactional
    public List<CompanyStaffResponse> listStaff() {
        UserAll company = currentCompany();
        return companyStaffRepository.findByCompany(company).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private UserAll currentCompany() {
        UserAll account = accountLookupService.getCurrentAccount();
        if (!AccountRole.COMPANY.getLabel().equals(account.getUserType())) {
            throw new RuntimeException("只有家政公司可以管理人员");
        }
        return account;
    }

    private String normalizeRemarks(String remarks) {
        if (remarks == null) {
            return null;
        }
        String trimmed = remarks.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private CompanyStaffResponse mapToResponse(CompanyStaff staff) {
        return new CompanyStaffResponse(
            staff.getId(),
            staff.getStaffName(),
            staff.getStaffPhone(),
            staff.getRemarks(),
            staff.getCreatedAt(),
            staff.getUpdatedAt()
        );
    }
}
