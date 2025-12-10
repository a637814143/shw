package com.example.housekeeping.service;

import com.example.housekeeping.dto.AssignStaffRequest;
import com.example.housekeeping.dto.CompanyStaffRequest;
import com.example.housekeeping.dto.CompanyStaffResponse;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.dto.TimeSlotAvailabilityResponse;
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
import com.example.housekeeping.service.ServiceTimeSlotHelper.SlotDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 家政公司人员管理。
 */
@Service
public class CompanyStaffService {

    private static final List<ServiceTimeSlotHelper.SlotDefinition> ALLOWED_SERVICE_SLOTS =
        ServiceTimeSlotHelper.AVAILABLE_SLOTS;

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
        refreshStaffAssignments(List.of(staff));
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
        refreshStaffAssignments(List.of(staff));
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
        SlotDefinition orderSlot = ServiceTimeSlotHelper.resolveByInstant(order.getScheduledAt(), ZoneId.systemDefault())
            .orElseThrow(() -> new RuntimeException("预约时间不在允许的服务时间段内"));
        if (!supportsSlot(staff, orderSlot)) {
            throw new RuntimeException("该人员未开放该服务时间段");
        }
        List<CompanyStaff> involvedStaff = List.of(staff);
        HashMap<Long, List<ServiceOrder>> ordersMap = loadAssignedOrdersByStaff(involvedStaff);
        boolean busyForSlot = isStaffBusyForSlot(staff, orderSlot, order.getScheduledAt(), ordersMap, order.getId());
        if (busyForSlot && (order.getAssignedStaff() == null
            || !order.getAssignedStaff().getId().equals(staff.getId()))) {
            throw new RuntimeException("该人员在该时间段已被指派");
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
        refreshStaffAssignments(involvedStaff, ordersMap, Instant.now());
        ServiceOrder saved = serviceOrderRepository.save(order);
        return serviceOrderService.mapToResponse(saved);
    }

    @Transactional
    public List<CompanyStaffResponse> listStaff(String keyword, Long categoryId, Instant scheduledAt) {
        UserAll company = ensureCompanyAccount();
        String normalized = normalizeOptional(keyword);
        ServiceCategory category = categoryId == null ? null : resolveCategory(categoryId);
        List<CompanyStaff> staff = category == null
            ? companyStaffRepository.findByCompany(company)
            : companyStaffRepository.findByCompanyAndCategory(company, category);
        SlotDefinition desiredSlot = scheduledAt == null
            ? null
            : ServiceTimeSlotHelper.resolveByInstant(scheduledAt, ZoneId.systemDefault()).orElse(null);
        if (desiredSlot != null) {
            staff = staff.stream().filter(item -> supportsSlot(item, desiredSlot)).collect(Collectors.toList());
        }
        if (normalized != null) {
            staff = staff.stream()
                .filter(item -> matchesKeyword(item, normalized))
                .collect(Collectors.toList());
        }
        HashMap<Long, List<ServiceOrder>> ordersMap = loadAssignedOrdersByStaff(staff);
        refreshStaffAssignments(staff, ordersMap, Instant.now());
        return staff.stream()
            .sorted(Comparator.comparing(CompanyStaff::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
            .map(item -> {
                boolean busyForSlot = desiredSlot != null
                    && isStaffBusyForSlot(item, desiredSlot, scheduledAt, ordersMap, null);
                return map(item, desiredSlot == null ? null : busyForSlot);
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countAvailableStaffForCompanyCategory(UserAll company, ServiceCategory category) {
        if (company == null || category == null) {
            return 0L;
        }
        List<CompanyStaff> staff = companyStaffRepository.findByCompanyAndCategory(company, category);
        HashMap<Long, List<ServiceOrder>> ordersMap = loadAssignedOrdersByStaff(staff);
        Instant now = Instant.now();
        refreshStaffAssignments(staff, ordersMap, now);
        return staff.stream()
            .filter(item -> !isStaffBusyAtReference(item, ordersMap, now))
            .count();
    }

    @Transactional(readOnly = true)
    public long countAvailableStaffForSlot(HousekeepService service, LocalDate date, SlotDefinition slot) {
        if (service == null || slot == null || date == null) {
            return 0L;
        }
        ServiceCategory category = service.getCategory();
        if (category == null) {
            return 0L;
        }
        List<CompanyStaff> staff = companyStaffRepository.findByCompanyAndCategory(service.getCompany(), category);
        staff = staff.stream().filter(member -> supportsSlot(member, slot)).collect(Collectors.toList());
        HashMap<Long, List<ServiceOrder>> ordersMap = loadAssignedOrdersByStaff(staff);
        Instant slotStart = ServiceTimeSlotHelper.buildStartInstant(date, slot, ZoneId.systemDefault());
        return staff.stream()
            .filter(member -> !isStaffBusyForSlot(member, slot, slotStart, ordersMap, null))
            .count();
    }

    @Transactional(readOnly = true)
    public List<TimeSlotAvailabilityResponse> buildSlotAvailability(HousekeepService service, LocalDate date) {
        if (service == null || date == null) {
            return List.of();
        }
        ServiceCategory category = service.getCategory();
        if (category == null) {
            return List.of();
        }
        List<CompanyStaff> staff = companyStaffRepository.findByCompanyAndCategory(service.getCompany(), category);
        HashMap<Long, List<ServiceOrder>> ordersMap = loadAssignedOrdersByStaff(staff);
        return ServiceTimeSlotHelper.AVAILABLE_SLOTS.stream()
            .map(slot -> {
                List<CompanyStaff> supportingStaff = staff.stream()
                    .filter(member -> supportsSlot(member, slot))
                    .collect(Collectors.toList());
                Instant slotStart = ServiceTimeSlotHelper.buildStartInstant(date, slot, ZoneId.systemDefault());
                long available = supportingStaff.stream()
                    .filter(member -> !isStaffBusyForSlot(member, slot, slotStart, ordersMap, null))
                    .count();
                return new TimeSlotAvailabilityResponse(
                    slot.getKey(),
                    slot.getLabel(),
                    available,
                    supportingStaff.size()
                );
            })
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
        refreshStaffAssignments(staffList);
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
        return map(staff, null);
    }

    private CompanyStaffResponse map(CompanyStaff staff, Boolean overrideAssigned) {
        ServiceCategory category = staff.getCategory();
        boolean assigned = overrideAssigned == null ? staff.isAssigned() : overrideAssigned;
        return new CompanyStaffResponse(
            staff.getId(),
            staff.getName(),
            staff.getContact(),
            staff.getNotes(),
            staff.getCreatedAt(),
            staff.getUpdatedAt(),
            category == null ? null : category.getId(),
            category == null ? null : category.getName(),
            assigned,
            parseServiceSlots(staff.getServiceTimeSlots())
        );
    }

    private Optional<SlotDefinition> resolveSlotFromString(String value) {
        if (value == null) {
            return Optional.empty();
        }
        return ServiceTimeSlotHelper.resolveByKey(value);
    }

    private Optional<SlotDefinition> resolveSlotFromInstant(Instant instant) {
        return ServiceTimeSlotHelper.resolveByInstant(instant, ZoneId.systemDefault());
    }

    private boolean supportsSlot(CompanyStaff staff, SlotDefinition slot) {
        if (staff == null || slot == null) {
            return false;
        }
        return parseServiceSlots(staff.getServiceTimeSlots()).stream()
            .map(this::resolveSlotFromString)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .anyMatch(item -> item.getStartHour() == slot.getStartHour() && item.getStartMinute() == slot.getStartMinute());
    }

    private HashMap<Long, List<ServiceOrder>> loadAssignedOrdersByStaff(List<CompanyStaff> staff) {
        HashMap<Long, List<ServiceOrder>> result = new HashMap<>();
        if (staff == null || staff.isEmpty()) {
            return result;
        }
        List<ServiceOrder> orders = serviceOrderRepository.findByAssignedStaffIn(staff);
        for (ServiceOrder order : orders) {
            if (order.getAssignedStaff() == null || order.getAssignedStaff().getId() == null) {
                continue;
            }
            Long staffId = order.getAssignedStaff().getId();
            result.computeIfAbsent(staffId, key -> new ArrayList<>()).add(order);
        }
        return result;
    }

    private boolean isStaffBusyForSlot(CompanyStaff staff, SlotDefinition targetSlot, Instant targetStart,
                                       HashMap<Long, List<ServiceOrder>> ordersMap, Long ignoreOrderId) {
        if (staff == null || targetSlot == null || targetStart == null) {
            return false;
        }
        List<ServiceOrder> orders = ordersMap.getOrDefault(staff.getId(), List.of());
        for (ServiceOrder order : orders) {
            if (ignoreOrderId != null && ignoreOrderId.equals(order.getId())) {
                continue;
            }
            SlotDefinition orderSlot = resolveSlotFromInstant(order.getScheduledAt()).orElse(null);
            if (orderSlot == null) {
                continue;
            }
            if (ServiceTimeSlotHelper.overlaps(targetStart, targetSlot, order.getScheduledAt(), orderSlot)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStaffBusyAtReference(CompanyStaff staff, HashMap<Long, List<ServiceOrder>> ordersMap, Instant reference) {
        if (staff == null || reference == null) {
            return false;
        }
        List<ServiceOrder> orders = ordersMap.getOrDefault(staff.getId(), List.of());
        for (ServiceOrder order : orders) {
            SlotDefinition orderSlot = resolveSlotFromInstant(order.getScheduledAt()).orElse(null);
            if (orderSlot == null) {
                continue;
            }
            Instant busyEnd = ServiceTimeSlotHelper.calculateBusyEnd(order.getScheduledAt(), orderSlot);
            if (busyEnd != null && busyEnd.isAfter(reference)) {
                return true;
            }
        }
        return false;
    }

    private void refreshStaffAssignments(List<CompanyStaff> staff) {
        refreshStaffAssignments(staff, loadAssignedOrdersByStaff(staff), Instant.now());
    }

    private void refreshStaffAssignments(List<CompanyStaff> staff, HashMap<Long, List<ServiceOrder>> ordersMap, Instant reference) {
        if (staff == null || staff.isEmpty()) {
            return;
        }
        Instant now = reference == null ? Instant.now() : reference;
        for (CompanyStaff item : staff) {
            boolean busy = isStaffBusyAtReference(item, ordersMap, now);
            if (item.isAssigned() != busy) {
                item.setAssigned(busy);
                item.setUpdatedAt(now);
                companyStaffRepository.save(item);
            }
        }
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
        List<String> normalizedSlots = sanitized.stream()
            .map(this::resolveSlotFromString)
            .map(opt -> opt.orElseThrow(() -> new RuntimeException("存在无效的服务时间段")))
            .map(ServiceTimeSlotHelper::normalizeLabel)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        if (normalizedSlots.isEmpty()) {
            throw new RuntimeException("存在无效的服务时间段");
        }
        Set<String> allowedLabels = ALLOWED_SERVICE_SLOTS.stream()
            .map(ServiceTimeSlotHelper::normalizeLabel)
            .collect(Collectors.toSet());
        boolean hasInvalid = normalizedSlots.stream().anyMatch(item -> !allowedLabels.contains(item));
        if (hasInvalid) {
            throw new RuntimeException("存在无效的服务时间段");
        }
        return normalizedSlots;
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
