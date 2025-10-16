package com.example.housekeeping.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
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

    @Transactional
    public ServiceOrderResponse createOrder(ServiceOrderRequest request) {
        UserAll user = accountLookupService.getCurrentAccount();
        ensureRole(user, AccountRole.USER);

        HousekeepService service = housekeepServiceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new RuntimeException("服务不存在"));

        ServiceOrder order = new ServiceOrder();
        order.setService(service);
        order.setUser(user);
        order.setStatus(ServiceOrderStatus.PENDING);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(order.getCreatedAt());

        return mapToResponse(serviceOrderRepository.save(order));
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
            order.setStatus(ServiceOrderStatus.REFUND_APPROVED);
        } else {
            order.setStatus(ServiceOrderStatus.REFUND_REJECTED);
        }
        order.setRefundResponse(normalizeMessage(request.getMessage()));
        order.setHandledBy(actor);
        order.setUpdatedAt(Instant.now());

        return mapToResponse(serviceOrderRepository.save(order));
    }

    private void ensureRole(UserAll account, AccountRole expectedRole) {
        if (!expectedRole.getLabel().equals(account.getUserType())) {
            throw new RuntimeException("权限不足");
        }
    }

    private ServiceOrderResponse mapToResponse(ServiceOrder order) {
        HousekeepService service = order.getService();
        return new ServiceOrderResponse(
            order.getId(),
            service.getId(),
            service.getName(),
            service.getUnit(),
            service.getPrice(),
            service.getContact(),
            service.getCompany().getUsername(),
            order.getUser().getUsername(),
            order.getStatus(),
            order.getRefundReason(),
            order.getRefundResponse(),
            order.getHandledBy() == null ? null : order.getHandledBy().getUsername(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }

    private String normalizeMessage(String message) {
        if (message == null) {
            return null;
        }
        String trimmed = message.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
