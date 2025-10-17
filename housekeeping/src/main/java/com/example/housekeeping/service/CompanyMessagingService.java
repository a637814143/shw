package com.example.housekeeping.service;

import com.example.housekeeping.dto.CompanyConversationSummaryResponse;
import com.example.housekeeping.dto.CompanyMessageRequest;
import com.example.housekeeping.dto.CompanyMessageResponse;
import com.example.housekeeping.entity.CompanyMessage;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.CompanyMessageRepository;
import com.example.housekeeping.repository.HousekeepServiceRepository;
import com.example.housekeeping.repository.ServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 企业与客户消息沟通服务。
 */
@Service
public class CompanyMessagingService {

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private HousekeepServiceRepository housekeepServiceRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private CompanyMessageRepository companyMessageRepository;

    @Transactional(readOnly = true)
    public List<CompanyConversationSummaryResponse> listConversationSummaries() {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);

        List<HousekeepService> services = housekeepServiceRepository.findByCompany(company);
        if (services.isEmpty()) {
            return Collections.emptyList();
        }

        List<ServiceOrder> orders = serviceOrderRepository.findByServiceIn(services);
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        return orders.stream()
            .map(order -> buildSummaryForOrder(order, company))
            .sorted(Comparator.comparing(CompanyConversationSummaryResponse::getLastMessageAt,
                Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompanyMessageResponse> listMessages(Long orderId, Instant since) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);

        ServiceOrder order = loadOrderForCompany(orderId, company);

        List<CompanyMessage> messages = since != null
            ? companyMessageRepository.findByOrderAndCreatedAtAfterOrderByCreatedAtAsc(order, since)
            : companyMessageRepository.findByOrderOrderByCreatedAtAsc(order);

        return messages.stream()
            .map(message -> mapToResponse(message, company))
            .collect(Collectors.toList());
    }

    @Transactional
    public CompanyMessageResponse sendMessage(Long orderId, CompanyMessageRequest request) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);

        ServiceOrder order = loadOrderForCompany(orderId, company);
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("消息内容不能为空");
        }

        CompanyMessage message = new CompanyMessage();
        message.setOrder(order);
        message.setSender(company);
        message.setRecipient(order.getUser());
        message.setContent(request.getContent().trim());
        message.setCreatedAt(Instant.now());

        CompanyMessage saved = companyMessageRepository.save(message);
        return mapToResponse(saved, company);
    }

    @Transactional
    public void markConversationRead(Long orderId) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);

        ServiceOrder order = loadOrderForCompany(orderId, company);
        List<CompanyMessage> unread = companyMessageRepository.findByOrderAndRecipientAndReadAtIsNull(order, company);
        if (unread.isEmpty()) {
            return;
        }
        Instant now = Instant.now();
        unread.forEach(message -> message.setReadAt(now));
        companyMessageRepository.saveAll(unread);
    }

    private CompanyConversationSummaryResponse buildSummaryForOrder(ServiceOrder order, UserAll company) {
        Optional<CompanyMessage> lastMessageOpt = companyMessageRepository.findFirstByOrderOrderByCreatedAtDesc(order);
        String lastMessage = lastMessageOpt.map(CompanyMessage::getContent)
            .orElse("尚未开始沟通");
        Instant lastMessageAt = lastMessageOpt.map(CompanyMessage::getCreatedAt)
            .orElse(order.getUpdatedAt());
        long unread = companyMessageRepository.countByOrderAndRecipientAndReadAtIsNull(order, company);

        return new CompanyConversationSummaryResponse(
            order.getId(),
            order.getService().getName(),
            order.getUser().getUsername(),
            order.getStatus(),
            lastMessage,
            lastMessageAt,
            unread
        );
    }

    private CompanyMessageResponse mapToResponse(CompanyMessage message, UserAll current) {
        boolean incoming = !Objects.equals(message.getSender().getId(), current.getId());
        return new CompanyMessageResponse(
            message.getId(),
            message.getOrder().getId(),
            message.getOrder().getService().getName(),
            message.getSender().getUsername(),
            message.getRecipient().getUsername(),
            message.getContent(),
            message.getCreatedAt(),
            message.getReadAt(),
            incoming
        );
    }

    private ServiceOrder loadOrderForCompany(Long orderId, UserAll company) {
        return serviceOrderRepository.findById(orderId)
            .filter(order -> {
                HousekeepService service = order.getService();
                return service != null && Objects.equals(service.getCompany().getId(), company.getId());
            })
            .orElseThrow(() -> new RuntimeException("未找到对应的订单或无权访问"));
    }

    private void ensureRole(UserAll account, AccountRole expectedRole) {
        if (!expectedRole.getLabel().equals(account.getUserType())) {
            throw new RuntimeException("权限不足");
        }
    }
}
