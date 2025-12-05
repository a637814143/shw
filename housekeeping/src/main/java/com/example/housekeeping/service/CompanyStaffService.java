package com.example.housekeeping.service;

import com.example.housekeeping.dto.AssignStaffRequest;
import com.example.housekeeping.dto.CompanyStaffRequest;
import com.example.housekeeping.dto.CompanyStaffResponse;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.entity.CompanyStaff;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.enums.ServiceOrderStatus;
import com.example.housekeeping.repository.CompanyStaffRepository;
import com.example.housekeeping.repository.ServiceCategoryRepository;
import com.example.housekeeping.repository.ServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 家政公司人员管理。
 */
@Service
public class CompanyStaffService {

    private static final List<String> ALLOWED_SERVICE_SLOTS = List.of(
        "8:00-10:00",
        "11:00-13:00",
        "14:00-16:00",
        "17:00-19:00",
        "20:00-22:00"
    );

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private CompanyStaffRepository companyStaffRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private ServiceOrderService serviceOrderService;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Transactional
    public CompanyStaffResponse createStaff(CompanyStaffRequest request) {
        UserAll company = ensureCompanyAccount();
        CompanyStaff staff = new CompanyStaff();
        staff.setCompany(company);
        applyRequest(staff, request);
        staff.setCreatedAt(Instant.now());
        staff.setUpdatedAt(staff.getCreatedAt());
        staff.setAssigned(false);
        CompanyStaff saved = companyStaffRepository.save(staff);
        return map(saved);
    }

    @Transactional
    public CompanyStaffResponse updateStaff(Long staffId, CompanyStaffRequest request) {
        CompanyStaff staff = ensureStaffBelongsToCurrentCompany(staffId);
        if (staff.isAssigned()) {
            Long originalCategoryId = staff.getCategory() == null ? null : staff.getCategory().getId();
            if (!Objects.equals(originalCategoryId, request.getCategoryId())) {
                throw new RuntimeException("该人员已被指派，无法变更服务分类");
            }
        }
        applyRequest(staff, request);
        staff.setUpdatedAt(Instant.now());
        CompanyStaff saved = companyStaffRepository.save(staff);
        return map(saved);
    }

    @Transactional
    public void deleteStaff(Long staffId) {
        CompanyStaff staff = ensureStaffBelongsToCurrentCompany(staffId);
        if (staff.isAssigned()) {
            throw new RuntimeException("该人员已被指派，请先完成或取消当前预约");
        }
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
        HousekeepService service = order.getService();
        ServiceCategory category = service.getCategory();
        if (category == null || staff.getCategory() == null
            || !category.getId().equals(staff.getCategory().getId())) {
            throw new RuntimeException("该人员不属于当前服务的分类");
        }
        if (staff.isAssigned() && (order.getAssignedStaff() == null
            || !order.getAssignedStaff().getId().equals(staff.getId()))) {
            throw new RuntimeException("该人员已指派至其他预约");
        }
        CompanyStaff previous = order.getAssignedStaff();
        if (previous != null && !previous.getId().equals(staff.getId())) {
            previous.setAssigned(false);
            previous.setUpdatedAt(Instant.now());
            companyStaffRepository.save(previous);
        }
        String workerName = staff.getName() == null ? "" : staff.getName().trim();
        String workerContact = staff.getContact() == null ? "" : staff.getContact().trim();
        order.setAssignedWorker(workerName.isEmpty() ? null : workerName);
        order.setWorkerContact(workerContact.isEmpty() ? null : workerContact);
        order.setAssignedStaff(staff);
        ServiceOrderStatus status = order.getStatus();
        if (status == null || status == ServiceOrderStatus.SCHEDULED || status == ServiceOrderStatus.PENDING) {
            String phonePart = workerContact.isEmpty() ? "" : "（" + workerContact + "）";
            order.setProgressNote("已安排" + workerName + phonePart + "上门服务");
            order.setStatus(ServiceOrderStatus.IN_PROGRESS);
        }
        order.setUpdatedAt(Instant.now());
        staff.setAssigned(true);
        staff.setUpdatedAt(Instant.now());
        companyStaffRepository.save(staff);
        ServiceOrder saved = serviceOrderRepository.save(order);
        return serviceOrderService.mapToResponse(saved);
    }

    @Transactional
    public List<CompanyStaffResponse> listStaff(String keyword, Long categoryId) {
        UserAll company = ensureCompanyAccount();
        String normalized = normalizeOptional(keyword);
        ServiceCategory category = categoryId == null ? null : resolveCategory(categoryId);
        List<CompanyStaff> staff = category == null
            ? companyStaffRepository.findByCompany(company)
            : companyStaffRepository.findByCompanyAndCategory(company, category);
        if (normalized != null) {
            staff = staff.stream()
                .filter(item -> matchesKeyword(item, normalized))
                .collect(Collectors.toList());
        }
        return staff.stream()
            .sorted(Comparator.comparing(CompanyStaff::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
            .map(this::map)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStaff(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        UserAll company = ensureCompanyAccount();
        List<Long> distinct = ids.stream()
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        if (distinct.isEmpty()) {
            return;
        }

        List<CompanyStaff> staffList = companyStaffRepository.findAllById(distinct);
        if (staffList.size() != distinct.size()) {
            throw new RuntimeException("部分人员不存在或已被删除");
        }
        for (CompanyStaff staff : staffList) {
            if (!staff.getCompany().getId().equals(company.getId())) {
                throw new RuntimeException("无法操作其他公司的人员");
            }
            if (staff.isAssigned()) {
                throw new RuntimeException("部分人员已被指派，无法删除");
            }
        }
        companyStaffRepository.deleteAll(staffList);
    }

    private void applyRequest(CompanyStaff staff, CompanyStaffRequest request) {
        staff.setName(request.getName().trim());
        staff.setContact(request.getContact().trim());
        ServiceCategory category = resolveCategory(request.getCategoryId());
        staff.setCategory(category);
        staff.setRole(null);
        staff.setNotes(normalizeOptional(request.getNotes()));
        staff.setServiceTimeSlots(String.join(",", normalizeServiceSlots(request.getServiceTimeSlots())));
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
        ServiceCategory category = staff.getCategory();
        return new CompanyStaffResponse(
            staff.getId(),
            staff.getName(),
            staff.getContact(),
            staff.getNotes(),
            staff.getCreatedAt(),
            staff.getUpdatedAt(),
            category == null ? null : category.getId(),
            category == null ? null : category.getName(),
            staff.isAssigned(),
            parseServiceSlots(staff.getServiceTimeSlots())
        );
    }

    private boolean matchesKeyword(CompanyStaff staff, String keyword) {
        String lower = keyword.toLowerCase();
        return Stream.of(
                staff.getName(),
                staff.getContact(),
                staff.getNotes(),
                staff.getServiceTimeSlots(),
                staff.getCategory() == null ? null : staff.getCategory().getName()
            )
            .filter(Objects::nonNull)
            .map(String::toLowerCase)
            .anyMatch(value -> value.contains(lower));
    }

    private ServiceCategory resolveCategory(Long categoryId) {
        if (categoryId == null) {
            throw new RuntimeException("请选择服务分类");
        }
        return serviceCategoryRepository.findById(categoryId)
            .orElseThrow(() -> new RuntimeException("服务分类不存在"));
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<String> normalizeServiceSlots(List<String> slots) {
        if (slots == null || slots.isEmpty()) {
            throw new RuntimeException("请选择至少一个服务时间段");
        }
        List<String> sanitized = slots.stream()
            .filter(Objects::nonNull)
            .map(String::trim)
            .filter(item -> !item.isEmpty())
            .distinct()
            .collect(Collectors.toList());
        if (sanitized.isEmpty()) {
            throw new RuntimeException("请选择至少一个服务时间段");
        }
        boolean hasInvalid = sanitized.stream().anyMatch(item -> !ALLOWED_SERVICE_SLOTS.contains(item));
        if (hasInvalid) {
            throw new RuntimeException("存在无效的服务时间段");
        }
        return sanitized;
    }

    private List<String> parseServiceSlots(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return List.of();
        }
        return Stream.of(raw.split(","))
            .map(String::trim)
            .filter(item -> !item.isEmpty())
            .collect(Collectors.toList());
    }
}
