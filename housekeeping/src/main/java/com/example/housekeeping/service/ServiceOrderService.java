package com.example.housekeeping.service;

import com.example.housekeeping.dto.AssignWorkerRequest;
import com.example.housekeeping.dto.OrderProgressUpdateRequest;
import com.example.housekeeping.dto.RefundDecisionRequest;
import com.example.housekeeping.dto.RefundRequest;
import com.example.housekeeping.dto.ServiceOrderRequest;
import com.example.housekeeping.dto.ServiceOrderResponse;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.enums.ServiceOrderStatus;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        UserAll company = service.getCompany();
        user.setMoney(user.getMoney().subtract(price));
        company.setMoney(company.getMoney().add(price));
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
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(order.getCreatedAt());

        ServiceOrder saved = serviceOrderRepository.save(order);
        userAllRepository.save(user);
        userAllRepository.save(company);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listOrdersForCurrentUser() {
        UserAll user = accountLookupService.getCurrentAccount();
        ensureRole(user, AccountRole.USER);
        return serviceOrderRepository.findByUserOrderByCreatedAtDesc(user)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listActiveOrdersForCompany() {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);
        List<HousekeepService> services = housekeepServiceRepository.findByCompany(company);
        if (services.isEmpty()) {
            return List.of();
        }
        return serviceOrderRepository.findByServiceIn(services).stream()
            .filter(order -> order.getStatus() == ServiceOrderStatus.SCHEDULED
                || order.getStatus() == ServiceOrderStatus.IN_PROGRESS
                || order.getStatus() == ServiceOrderStatus.PENDING)
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

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listRefundRequestsForCompany() {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);
        List<HousekeepService> services = housekeepServiceRepository.findByCompany(company);
        if (services.isEmpty()) {
            return List.of();
        }
        return serviceOrderRepository.findByServiceInAndStatus(services, ServiceOrderStatus.REFUND_REQUESTED)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
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
        return mapToResponse(serviceOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listRefundRequestsForAdmin() {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureRole(admin, AccountRole.ADMIN);
        return serviceOrderRepository.findAll().stream()
            .filter(order -> order.getStatus() == ServiceOrderStatus.REFUND_REQUESTED)
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ServiceOrderResponse handleRefund(Long orderId, RefundDecisionRequest request) {
        UserAll actor = accountLookupService.getCurrentAccount();
        ServiceOrder order = serviceOrderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        boolean isAdmin = AccountRole.ADMIN.getLabel().equals(actor.getUserType());
        boolean isCompany = AccountRole.COMPANY.getLabel().equals(actor.getUserType());
        if (isCompany && !order.getService().getCompany().getId().equals(actor.getId())) {
            throw new RuntimeException("无权处理其他公司的退款");
        }
        if (!isAdmin && !isCompany) {
            throw new RuntimeException("没有处理权限");
        }
        if (order.getStatus() != ServiceOrderStatus.REFUND_REQUESTED) {
            throw new RuntimeException("订单当前无需处理");
        }

        if (Boolean.TRUE.equals(request.getApprove())) {
            BigDecimal amount = order.getAmount();
            if (amount == null) {
                throw new RuntimeException("订单金额异常");
            }
            UserAll user = order.getUser();
            UserAll company = order.getService().getCompany();
            if (company.getMoney().compareTo(amount) < 0) {
                throw new RuntimeException("家政公司余额不足，无法退款");
            }
            user.setMoney(user.getMoney().add(amount));
            company.setMoney(company.getMoney().subtract(amount));
            int earned = safeLoyalty(order.getLoyaltyPoints());
            user.setLoyaltyPoints(Math.max(0, safeLoyalty(user.getLoyaltyPoints()) - earned));
            order.setStatus(ServiceOrderStatus.REFUND_APPROVED);
            order.setProgressNote("订单已退款");
        } else {
            order.setStatus(ServiceOrderStatus.REFUND_REJECTED);
            order.setProgressNote("退款申请被拒绝");
        }
        order.setRefundResponse(normalizeMessage(request.getMessage()));
        order.setHandledBy(actor);
        order.setUpdatedAt(Instant.now());

        ServiceOrder saved = serviceOrderRepository.save(order);
        userAllRepository.save(order.getUser());
        userAllRepository.save(order.getService().getCompany());
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

        order.setAssignedWorker(workerName);
        order.setWorkerContact(workerContact);
        if (order.getStatus() == ServiceOrderStatus.SCHEDULED || order.getStatus() == ServiceOrderStatus.PENDING) {
            order.setProgressNote("已安排 " + workerName + " 负责服务");
        }
        order.setUpdatedAt(Instant.now());
        return mapToResponse(serviceOrderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public List<ServiceOrderResponse> listOrdersForAdmin() {
        UserAll admin = accountLookupService.getCurrentAccount();
        ensureRole(admin, AccountRole.ADMIN);
        return serviceOrderRepository.findAll().stream()
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
            order.getUpdatedAt()
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
}
