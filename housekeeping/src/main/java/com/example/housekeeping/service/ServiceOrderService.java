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
import com.example.housekeeping.repository.UserAllRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        UserAll company = service.getCompany();
        user.setMoney(user.getMoney().subtract(price));
        company.setMoney(company.getMoney().add(price));

        ServiceOrder order = new ServiceOrder();
        order.setService(service);
        order.setUser(user);
        order.setAmount(price);
        order.setStatus(ServiceOrderStatus.PENDING);
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
            order.setStatus(ServiceOrderStatus.REFUND_APPROVED);
        } else {
            order.setStatus(ServiceOrderStatus.REFUND_REJECTED);
        }
        order.setRefundResponse(normalizeMessage(request.getMessage()));
        order.setHandledBy(actor);
        order.setUpdatedAt(Instant.now());

        ServiceOrder saved = serviceOrderRepository.save(order);
        userAllRepository.save(order.getUser());
        userAllRepository.save(order.getService().getCompany());
        return mapToResponse(saved);
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
            order.getAmount(),
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
