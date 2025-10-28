package com.example.housekeeping.service;

import com.example.housekeeping.dto.AssignWorkerRequest;
import com.example.housekeeping.dto.OrderProgressUpdateRequest;
import com.example.housekeeping.dto.RefundDecisionRequest;
import com.example.housekeeping.dto.RefundRequest;
import com.example.housekeeping.dto.ServiceOrderRequest;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.entity.CompanyStaff;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.ServiceCategory;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.enums.ServiceOrderStatus;
import com.example.housekeeping.repository.CompanyMessageRepository;
import com.example.housekeeping.repository.CompanyStaffRepository;
import com.example.housekeeping.repository.HousekeepServiceRepository;
import com.example.housekeeping.repository.ServiceOrderRepository;
import com.example.housekeeping.repository.UserAllRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 服务订单相关业务。
 */
@Service
public class ServiceOrderService {

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private HousekeepServiceRepository housekeepServiceRepository;

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private UserAllRepository userAllRepository;

    @Autowired
    private CompanyMessageRepository companyMessageRepository;

    @Autowired
    private CompanyStaffRepository companyStaffRepository;

    @Transactional
    public ServiceOrderResponse createOrder(ServiceOrderRequest request) {
        UserAll user = accountLookupService.getCurrentAccount();
        ensureRole(user, AccountRole.USER);

        HousekeepService service = housekeepServiceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new RuntimeException("服务不存在"));

        BigDecimal price = service.getPrice();
        if (price == null) {
            throw new RuntimeException("服务价格未设置");
        }
        if (user.getMoney().compareTo(price) < 0) {
            throw new RuntimeException("余额不足，请先充值");
        }

        Instant scheduledAt = Objects.requireNonNull(request.getScheduledAt(), "预约时间不能为空");
        if (scheduledAt.isBefore(Instant.now().minusSeconds(60))) {
            throw new RuntimeException("预约时间不能早于当前时间");
        }

        String specialRequest = normalizeMessage(request.getSpecialRequest());
        String serviceAddress = normalizeMessage(request.getServiceAddress());
        if (serviceAddress == null) {
            serviceAddress = normalizeMessage(user.getContactAddress());
        }
        int earnedPoints = calculateLoyaltyPoints(price);

        user.setMoney(user.getMoney().subtract(price));
        UserAll treasury = getTreasuryAccount();
        treasury.setMoney(treasury.getMoney().add(price));
        user.setLoyaltyPoints(safeLoyalty(user.getLoyaltyPoints()) + earnedPoints);

        ServiceOrder order = new ServiceOrder();
        order.setService(service);
        order.setUser(user);
        order.setAmount(price);
        order.setStatus(ServiceOrderStatus.SCHEDULED);
        order.setScheduledAt(scheduledAt);
        order.setSpecialRequest(specialRequest);
        order.setServiceAddress(serviceAddress);
        order.setProgressNote("待上门服务");
        order.setLoyaltyPoints(earnedPoints);
        order.setAssignedStaff(null);
        order.setAssignedWorker(null);
        order.setWorkerContact(null);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(order.getCreatedAt());
        order.setSettlementReleased(false);
        order.setSettlementReleasedAt(null);

        ServiceOrder saved = serviceOrderRepository.save(order);
        userAllRepository.save(user);
        userAllRepository.save(treasury);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listOrdersForCurrentUser(String keyword) {
        UserAll user = accountLookupService.getCurrentAccount();
        ensureRole(user, AccountRole.USER);
        String normalizedKeyword = normalizeKeyword(keyword);
        return serviceOrderRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .filter(order -> matchesOrderKeyword(order, normalizedKeyword))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listActiveOrdersForCompany(String keyword) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);
        List<HousekeepService> services = housekeepServiceRepository.findByCompany(company);
        if (services.isEmpty()) {
            return List.of();
        }
        String normalizedKeyword = normalizeKeyword(keyword);
        return serviceOrderRepository.findByServiceIn(services).stream()
            .filter(order -> order.getStatus() == ServiceOrderStatus.SCHEDULED
                || order.getStatus() == ServiceOrderStatus.IN_PROGRESS
                || order.getStatus() == ServiceOrderStatus.PENDING)
            .filter(order -> matchesOrderKeyword(order, normalizedKeyword))
            .sorted(Comparator.comparing(ServiceOrder::getScheduledAt))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ServiceOrderResponse requestRefund(Long orderId, RefundRequest request) {
        UserAll user = accountLookupService.getCurrentAccount();
        ensureRole(user, AccountRole.USER);

        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("只能对自己的订单申请退款");
        }
        if (order.getStatus() == ServiceOrderStatus.REFUND_APPROVED) {
            throw new RuntimeException("订单已退款");
        }
        order.setStatus(ServiceOrderStatus.REFUND_REQUESTED);
        order.setRefundReason(request.getReason().trim());
        order.setRefundResponse(null);
        order.setHandledBy(null);
        order.setProgressNote("用户申请退款");
        order.setUpdatedAt(Instant.now());
        return mapToResponse(serviceOrderRepository.save(order));
    }

    @Transactional
    public ServiceOrderResponse updateOrderProgress(Long orderId, OrderProgressUpdateRequest request) {
        UserAll actor = accountLookupService.getCurrentAccount();
        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        boolean isAdmin = AccountRole.ADMIN.getLabel().equals(actor.getUserType());
        boolean isCompany = AccountRole.COMPANY.getLabel().equals(actor.getUserType());
        if (!isAdmin && !isCompany) {
            throw new RuntimeException("没有操作权限");
        }
        if (isCompany && !order.getService().getCompany().getId().equals(actor.getId())) {
            throw new RuntimeException("无权管理其他公司的订单");
        }
        if (order.getStatus() == ServiceOrderStatus.REFUND_REQUESTED
            || order.getStatus() == ServiceOrderStatus.REFUND_APPROVED) {
            throw new RuntimeException("退款流程中的订单无法修改进度");
        }

        ServiceOrderStatus desiredStatus = normalizeProgressStatus(request.getStatus());
        order.setStatus(desiredStatus);
        String note = normalizeMessage(request.getProgressNote());
        if (note == null) {
            if (desiredStatus == ServiceOrderStatus.COMPLETED) {
                note = "服务已完成";
            } else if (desiredStatus == ServiceOrderStatus.IN_PROGRESS) {
                note = "服务进行中";
            } else {
                note = order.getProgressNote();
            }
        }
        order.setProgressNote(note);
        order.setUpdatedAt(Instant.now());
        if (desiredStatus == ServiceOrderStatus.COMPLETED
            || desiredStatus == ServiceOrderStatus.REFUND_APPROVED
            || desiredStatus == ServiceOrderStatus.REFUND_REJECTED) {
            releaseAssignedStaff(order);
        }
        return mapToResponse(serviceOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listRefundRequestsForAdmin() {
        return listRefundsForAdmin("pending", null);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listRefundsForAdmin(String stage, String keyword) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureRole(admin, AccountRole.ADMIN);
        List<ServiceOrderStatus> statuses = resolveRefundStatuses(stage);
        String normalizedKeyword = normalizeKeyword(keyword);
        return serviceOrderRepository.findAll().stream()
            .filter(order -> statuses.contains(order.getStatus()))
            .filter(order -> matchesRefundKeyword(order, normalizedKeyword))
            .sorted(Comparator.comparing(ServiceOrder::getUpdatedAt).reversed())
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ServiceOrderResponse handleRefund(Long orderId, RefundDecisionRequest request) {
        UserAll actor = accountLookupService.getCurrentAccount();
        ensureRole(actor, AccountRole.ADMIN);
        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.getStatus() != ServiceOrderStatus.REFUND_REQUESTED) {
            throw new RuntimeException("订单当前无需处理");
        }

        boolean approve = Boolean.TRUE.equals(request.getApprove());
        BigDecimal amount = order.getAmount();
        if (amount == null) {
            throw new RuntimeException("订单金额异常");
        }
        UserAll user = order.getUser();
        UserAll company = order.getService().getCompany();
        UserAll treasury = getTreasuryAccount();
        boolean wasSettled = order.isSettlementReleased();

        Instant now = Instant.now();

        if (approve) {
            if (wasSettled) {
                if (company.getMoney().compareTo(amount) < 0) {
                    throw new RuntimeException("家政公司余额不足，无法退款");
                }
                company.setMoney(company.getMoney().subtract(amount));
            } else {
                if (treasury.getMoney().compareTo(amount) < 0) {
                    throw new RuntimeException("平台余额不足，无法退款");
                }
                treasury.setMoney(treasury.getMoney().subtract(amount));
            }
            user.setMoney(user.getMoney().add(amount));
            int earned = safeLoyalty(order.getLoyaltyPoints());
            user.setLoyaltyPoints(Math.max(0, safeLoyalty(user.getLoyaltyPoints()) - earned));
            order.setStatus(ServiceOrderStatus.REFUND_APPROVED);
            order.setProgressNote("订单已退款");
            order.setSettlementReleased(true);
            order.setSettlementReleasedAt(now);
        } else {
            order.setStatus(ServiceOrderStatus.REFUND_REJECTED);
            order.setProgressNote("退款申请被拒绝");
        }

        order.setRefundResponse(normalizeMessage(request.getMessage()));
        order.setHandledBy(actor);
        order.setUpdatedAt(now);
        releaseAssignedStaff(order);

        ServiceOrder saved = serviceOrderRepository.save(order);
        if (approve) {
            userAllRepository.save(user);
            if (wasSettled) {
                userAllRepository.save(company);
            } else {
                userAllRepository.save(treasury);
            }
        }
        return mapToResponse(saved);
    }

    @Transactional
    public ServiceOrderResponse settleCompletedOrder(Long orderId) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureRole(admin, AccountRole.ADMIN);
        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (order.isSettlementReleased()) {
            throw new RuntimeException("该订单已完成结算");
        }
        if (order.getStatus() != ServiceOrderStatus.COMPLETED) {
            throw new RuntimeException("仅已完成的订单可以结算");
        }

        BigDecimal amount = order.getAmount();
        if (amount == null) {
            throw new RuntimeException("订单金额异常");
        }
        UserAll company = order.getService().getCompany();
        UserAll treasury = getTreasuryAccount();
        if (treasury.getMoney().compareTo(amount) < 0) {
            throw new RuntimeException("平台余额不足，请先充值");
        }

        treasury.setMoney(treasury.getMoney().subtract(amount));
        company.setMoney(company.getMoney().add(amount));
        Instant now = Instant.now();
        order.setSettlementReleased(true);
        order.setSettlementReleasedAt(now);
        order.setUpdatedAt(now);

        ServiceOrder saved = serviceOrderRepository.save(order);
        userAllRepository.save(treasury);
        userAllRepository.save(company);
        return mapToResponse(saved);
    }

    @Transactional
    public ServiceOrderResponse assignWorker(Long orderId, AssignWorkerRequest request) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureRole(admin, AccountRole.ADMIN);

        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        String workerName = normalizeMessage(request.getWorkerName());
        String workerContact = normalizeMessage(request.getWorkerContact());
        if (workerName == null || workerContact == null) {
            throw new RuntimeException("家政人员与联系方式不能为空");
        }

        releaseAssignedStaff(order);
        order.setAssignedWorker(workerName);
        order.setWorkerContact(workerContact);
        if (order.getStatus() == ServiceOrderStatus.SCHEDULED || order.getStatus() == ServiceOrderStatus.PENDING) {
            order.setProgressNote("已安排 " + workerName + " 负责服务");
        }
        order.setUpdatedAt(Instant.now());
        return mapToResponse(serviceOrderRepository.save(order));
    }

    @Transactional
    public void deleteProcessedRefunds(List<Long> ids) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureRole(admin, AccountRole.ADMIN);
        if (ids == null || ids.isEmpty()) {
            return;
        }
        List<Long> distinct = ids.stream()
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        if (distinct.isEmpty()) {
            return;
        }
        List<ServiceOrder> orders = serviceOrderRepository.findAllById(distinct);
        if (orders.size() != distinct.size()) {
            throw new RuntimeException("部分退款记录不存在或已被删除");
        }
        for (ServiceOrder order : orders) {
            ServiceOrderStatus status = order.getStatus();
            if (status != ServiceOrderStatus.REFUND_APPROVED && status != ServiceOrderStatus.REFUND_REJECTED) {
                throw new RuntimeException("仅已处理的退款记录可以删除");
            }
        }
        for (ServiceOrder order : orders) {
            companyMessageRepository.deleteByOrder(order);
        }
        serviceOrderRepository.deleteAll(orders);
    }

    @Transactional
    public void deleteOrdersForCompany(List<Long> ids) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);
        deleteOrders(ids, order -> order.getService() != null
            && order.getService().getCompany() != null
            && Objects.equals(order.getService().getCompany().getId(), company.getId()));
    }

    @Transactional
    public void deleteOrdersForCurrentUser(List<Long> ids) {
        UserAll user = accountLookupService.getCurrentAccount();
        ensureRole(user, AccountRole.USER);
        if (ids == null || ids.isEmpty()) {
            return;
        }
        Set<Long> distinctIds = ids.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(HashSet::new));
        if (distinctIds.isEmpty()) {
            return;
        }

        List<ServiceOrder> orders = serviceOrderRepository.findAllById(distinctIds);
        if (orders.size() != distinctIds.size()) {
            throw new RuntimeException("部分订单不存在或已被删除");
        }

        for (ServiceOrder order : orders) {
            if (order.getUser() == null || !Objects.equals(order.getUser().getId(), user.getId())) {
                throw new RuntimeException("只能删除当前账号的订单");
            }
            ServiceOrderStatus status = order.getStatus();
            if (status != ServiceOrderStatus.COMPLETED && status != ServiceOrderStatus.REFUND_APPROVED) {
                throw new RuntimeException("仅已完成或已退款的订单可以删除");
            }
        }

        orders.forEach(companyMessageRepository::deleteByOrder);
        serviceOrderRepository.deleteAll(orders);
    }

    @Transactional
    public void deleteOrdersForAdmin(List<Long> ids) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureRole(admin, AccountRole.ADMIN);
        if (ids == null || ids.isEmpty()) {
            return;
        }
        Set<Long> distinctIds = ids.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(HashSet::new));
        if (distinctIds.isEmpty()) {
            return;
        }

        List<ServiceOrder> orders = serviceOrderRepository.findAllById(distinctIds);
        if (orders.size() != distinctIds.size()) {
            throw new RuntimeException("部分订单不存在或已被删除");
        }

        for (ServiceOrder order : orders) {
            if (!order.isSettlementReleased()) {
                throw new RuntimeException("仅已结算的订单可以删除");
            }
        }

        orders.forEach(companyMessageRepository::deleteByOrder);
        serviceOrderRepository.deleteAll(orders);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listOrdersForAdmin(String keyword) {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureRole(admin, AccountRole.ADMIN);
        String normalizedKeyword = normalizeKeyword(keyword);
        return serviceOrderRepository.findAll().stream()
            .filter(order -> matchesOrderKeyword(order, normalizedKeyword))
            .sorted(Comparator.comparing(ServiceOrder::getCreatedAt).reversed())
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private void ensureRole(UserAll account, AccountRole expectedRole) {
        if (!expectedRole.getLabel().equals(account.getUserType())) {
            throw new RuntimeException("权限不足");
        }
    }

    ServiceOrderResponse mapToResponse(ServiceOrder order) {
        HousekeepService service = order.getService();
        ServiceCategory category = service.getCategory();
        Long assignedStaffId = order.getAssignedStaff() == null ? null : order.getAssignedStaff().getId();
        return new ServiceOrderResponse(
            order.getId(),
            service.getId(),
            service.getName(),
            service.getUnit(),
            order.getAmount(),
            service.getContact(),
            service.getCompany().getUsername(),
            order.getUser().getUsername(),
            order.getStatus(),
            order.getScheduledAt(),
            order.getSpecialRequest(),
            order.getServiceAddress(),
            order.getProgressNote(),
            order.getLoyaltyPoints() == null ? 0 : order.getLoyaltyPoints(),
            order.getRefundReason(),
            order.getRefundResponse(),
            order.getHandledBy() == null ? null : order.getHandledBy().getUsername(),
            order.getAssignedWorker(),
            order.getWorkerContact(),
            order.getUser().getContactPhone(),
            order.getUser().getContactAddress(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            order.isSettlementReleased(),
            order.getSettlementReleasedAt(),
            category == null ? null : category.getId(),
            category == null ? null : category.getName(),
            assignedStaffId
        );
    }

    private ServiceOrderStatus normalizeProgressStatus(ServiceOrderStatus status) {
        if (status == null) {
            return ServiceOrderStatus.SCHEDULED;
        }
        switch (status) {
            case REFUND_REQUESTED:
            case REFUND_APPROVED:
            case REFUND_REJECTED:
                throw new RuntimeException("无法将订单直接切换为退款状态");
            case PENDING:
                return ServiceOrderStatus.SCHEDULED;
            default:
                return status;
        }
    }

    private UserAll getTreasuryAccount() {
        return userAllRepository.findFirstByUserTypeOrderByIdAsc(AccountRole.ADMIN.getLabel())
            .orElseThrow(() -> new RuntimeException("平台管理员账户未配置"));
    }

    private int calculateLoyaltyPoints(BigDecimal amount) {
        if (amount == null) {
            return 0;
        }
        return amount.divide(BigDecimal.TEN, 0, RoundingMode.FLOOR).intValue();
    }

    private int safeLoyalty(Integer value) {
        return value == null ? 0 : Math.max(0, value);
    }

    private String normalizeMessage(String message) {
        if (message == null) {
            return null;
        }
        String trimmed = message.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeKeyword(String keyword) {
        return normalizeMessage(keyword);
    }

    private boolean matchesOrderKeyword(ServiceOrder order, String keyword) {
        if (keyword == null) {
            return true;
        }
        String lower = keyword.toLowerCase();
        return Stream.of(
                order.getService() != null ? order.getService().getName() : null,
                order.getService() != null && order.getService().getCompany() != null
                    ? order.getService().getCompany().getUsername() : null,
                order.getService() != null && order.getService().getCompany() != null
                    ? order.getService().getCompany().getDisplayName() : null,
                order.getService() != null ? order.getService().getContact() : null,
                order.getUser() != null ? order.getUser().getUsername() : null,
                order.getUser() != null ? order.getUser().getContactPhone() : null,
                order.getUser() != null ? order.getUser().getContactAddress() : null,
                order.getSpecialRequest(),
                order.getServiceAddress(),
                order.getProgressNote(),
                order.getRefundReason(),
                order.getRefundResponse(),
                order.getAssignedWorker(),
                order.getWorkerContact(),
                order.getStatus() == null ? null : order.getStatus().name(),
                order.isSettlementReleased() ? "已结算" : "待结算",
                order.getSettlementReleasedAt() == null ? null : order.getSettlementReleasedAt().toString()
            )
            .filter(Objects::nonNull)
            .map(String::toLowerCase)
            .anyMatch(value -> value.contains(lower));
    }

    private List<ServiceOrderStatus> resolveRefundStatuses(String stage) {
        if (stage == null) {
            return List.of(ServiceOrderStatus.REFUND_REQUESTED);
        }
        String normalized = stage.trim().toLowerCase();
        if (normalized.isEmpty() || "pending".equals(normalized)) {
            return List.of(ServiceOrderStatus.REFUND_REQUESTED);
        }
        if ("processed".equals(normalized)) {
            return List.of(ServiceOrderStatus.REFUND_APPROVED, ServiceOrderStatus.REFUND_REJECTED);
        }
        return List.of(
            ServiceOrderStatus.REFUND_REQUESTED,
            ServiceOrderStatus.REFUND_APPROVED,
            ServiceOrderStatus.REFUND_REJECTED
        );
    }

    private void releaseAssignedStaff(ServiceOrder order) {
        CompanyStaff assignedStaff = order.getAssignedStaff();
        if (assignedStaff != null) {
            assignedStaff.setAssigned(false);
            assignedStaff.setUpdatedAt(Instant.now());
            companyStaffRepository.save(assignedStaff);
            order.setAssignedStaff(null);
        }
    }

    private boolean matchesRefundKeyword(ServiceOrder order, String keyword) {
        if (keyword == null) {
            return true;
        }
        String lower = keyword.toLowerCase();
        return order.getService().getName().toLowerCase().contains(lower)
            || order.getUser().getUsername().toLowerCase().contains(lower)
            || order.getService().getCompany().getUsername().toLowerCase().contains(lower);
    }

    private void deleteOrders(List<Long> ids, Predicate<ServiceOrder> allowedPredicate) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        Set<Long> distinctIds = ids.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(HashSet::new));
        if (distinctIds.isEmpty()) {
            return;
        }
        List<ServiceOrder> orders = serviceOrderRepository.findAllById(distinctIds);
        List<ServiceOrder> permitted = orders.stream()
            .filter(allowedPredicate)
            .collect(Collectors.toList());
        if (permitted.isEmpty()) {
            return;
        }
        permitted.forEach(this::releaseAssignedStaff);
        permitted.forEach(companyMessageRepository::deleteByOrder);
        serviceOrderRepository.deleteAll(permitted);
    }
}
