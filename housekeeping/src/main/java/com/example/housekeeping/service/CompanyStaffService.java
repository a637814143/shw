package com.example.housekeeping.service;

import com.example.housekeeping.dto.AssignStaffRequest;
import com.example.housekeeping.dto.CompanyStaffRequest;
import com.example.housekeeping.dto.CompanyStaffResponse;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.entity.CompanyStaff;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.enums.ServiceOrderStatus;
import com.example.housekeeping.repository.CompanyStaffRepository;
import com.example.housekeeping.repository.ServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 家政公司人员管理。
 */
@Service
public class CompanyStaffService {

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private CompanyStaffRepository companyStaffRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Transactional
    public CompanyStaffResponse createStaff(CompanyStaffRequest request) {
        UserAll company = ensureCompanyAccount();
        CompanyStaff staff = new CompanyStaff();
        staff.setCompany(company);
        applyRequest(staff, request);
        staff.setCreatedAt(Instant.now());
        staff.setUpdatedAt(staff.getCreatedAt());
        CompanyStaff saved = companyStaffRepository.save(staff);
        return map(saved);
    }

    @Transactional
    public CompanyStaffResponse updateStaff(Long staffId, CompanyStaffRequest request) {
        CompanyStaff staff = ensureStaffBelongsToCurrentCompany(staffId);
        applyRequest(staff, request);
        staff.setUpdatedAt(Instant.now());
        CompanyStaff saved = companyStaffRepository.save(staff);
        return map(saved);
    }

    @Transactional
    public void deleteStaff(Long staffId) {
        CompanyStaff staff = ensureStaffBelongsToCurrentCompany(staffId);
        companyStaffRepository.delete(staff);
    }

    @Transactional
    public ServiceOrderResponse assignStaff(Long staffId, AssignStaffRequest request) {
        CompanyStaff staff = ensureStaffBelongsToCurrentCompany(staffId);
        Long orderId = request.getOrderId();
        if (orderId == null) {
            throw new RuntimeException("请选择要指派的订单");
        }
        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!order.getService().getCompany().getId().equals(staff.getCompany().getId())) {
            throw new RuntimeException("无法指派其他公司的订单");
        }
        order.setAssignedWorker(staff.getName());
        order.setWorkerContact(staff.getContact());
        ServiceOrderStatus status = order.getStatus();
        if (status == null || status == ServiceOrderStatus.SCHEDULED || status == ServiceOrderStatus.PENDING) {
            order.setProgressNote("已安排 " + staff.getName() + " 上门服务");
        }
        order.setUpdatedAt(Instant.now());
        ServiceOrder saved = serviceOrderRepository.save(order);
        return serviceOrderService.mapToResponse(saved);
    }

    @Transactional
    public List<CompanyStaffResponse> listStaff() {
        UserAll company = ensureCompanyAccount();
        return companyStaffRepository.findByCompany(company).stream()
            .map(this::map)
            .collect(Collectors.toList());
    }

    private void applyRequest(CompanyStaff staff, CompanyStaffRequest request) {
        staff.setName(request.getName().trim());
        staff.setContact(request.getContact().trim());
        staff.setRole(normalizeOptional(request.getRole()));
        staff.setNotes(normalizeOptional(request.getNotes()));
    }

    private CompanyStaff ensureStaffBelongsToCurrentCompany(Long staffId) {
        if (staffId == null) {
            throw new RuntimeException("请选择要操作的人员");
        }
        UserAll company = ensureCompanyAccount();
        CompanyStaff staff = companyStaffRepository.findById(staffId)
            .orElseThrow(() -> new RuntimeException("人员不存在"));
        if (!staff.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("无法操作其他公司的人员");
        }
        return staff;
    }

    private UserAll ensureCompanyAccount() {
        UserAll account = accountLookupService.getCurrentAccount();
        AccountRole role = AccountRole.fromValue(account.getUserType());
        if (role != AccountRole.COMPANY) {
            throw new RuntimeException("仅家政公司可以管理人员信息");
        }
        return account;
    }

    private CompanyStaffResponse map(CompanyStaff staff) {
        return new CompanyStaffResponse(
            staff.getId(),
            staff.getName(),
            staff.getContact(),
            staff.getRole(),
            staff.getNotes(),
            staff.getCreatedAt(),
            staff.getUpdatedAt()
        );
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
