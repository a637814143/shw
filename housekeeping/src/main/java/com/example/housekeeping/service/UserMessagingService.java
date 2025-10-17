package com.example.housekeeping.service;

import com.example.housekeeping.dto.CompanyMessageRequest;
import com.example.housekeeping.dto.CompanyMessageResponse;
import com.example.housekeeping.dto.UserConversationSummaryResponse;
import com.example.housekeeping.entity.CompanyMessage;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceOrder;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.CompanyMessageRepository;
import com.example.housekeeping.repository.ServiceOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 普通用户与家政公司的即时沟通。
 */
@Service
public class UserMessagingService {

    @Autowired
    private AccountLookupService accountLookupService;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private CompanyMessageRepository companyMessageRepository;

    @Transactional(readOnly = true)
    public List<UserConversationSummaryResponse> listConversationSummaries() {
        UserAll user = ensureUser();
        return serviceOrderRepository.findByUserOrderByCreatedAtDesc(user).stream()
            .map(order -> buildSummary(order, user))
            .sorted(Comparator.comparing(UserConversationSummaryResponse::getLastMessageAt,
                Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompanyMessageResponse> listMessages(Long orderId, Instant since) {
        UserAll user = ensureUser();
        ServiceOrder order = loadOrder(orderId, user);

        List<CompanyMessage> messages = since != null
            ? companyMessageRepository.findByOrderAndCreatedAtAfterOrderByCreatedAtAsc(order, since)
            : companyMessageRepository.findByOrderOrderByCreatedAtAsc(order);

        return messages.stream()
            .map(message -> mapToResponse(message, user))
            .collect(Collectors.toList());
    }

    @Transactional
    public CompanyMessageResponse sendMessage(Long orderId, CompanyMessageRequest request) {
        UserAll user = ensureUser();
        ServiceOrder order = loadOrder(orderId, user);
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new RuntimeException("消息内容不能为空");
        }

        HousekeepService service = order.getService();
        UserAll company = service.getCompany();

        CompanyMessage message = new CompanyMessage();
        message.setOrder(order);
        message.setSender(user);
        message.setRecipient(company);
        message.setContent(request.getContent().trim());
        message.setCreatedAt(Instant.now());

        CompanyMessage saved = companyMessageRepository.save(message);
        return mapToResponse(saved, user);
    }

    @Transactional
    public void markRead(Long orderId) {
        UserAll user = ensureUser();
        ServiceOrder order = loadOrder(orderId, user);
        List<CompanyMessage> unread = companyMessageRepository.findByOrderAndRecipientAndReadAtIsNull(order, user);
        if (unread.isEmpty()) {
            return;
        }
        Instant now = Instant.now();
        unread.forEach(message -> message.setReadAt(now));
        companyMessageRepository.saveAll(unread);
    }

    private UserConversationSummaryResponse buildSummary(ServiceOrder order, UserAll user) {
        Optional<CompanyMessage> lastMessageOpt = companyMessageRepository.findFirstByOrderOrderByCreatedAtDesc(order);
        String lastMessage = lastMessageOpt.map(CompanyMessage::getContent).orElse("尚未沟通");
        Instant lastMessageAt = lastMessageOpt.map(CompanyMessage::getCreatedAt).orElse(order.getUpdatedAt());
        long unread = companyMessageRepository.countByOrderAndRecipientAndReadAtIsNull(order, user);

        return new UserConversationSummaryResponse(
            order.getId(),
            order.getService().getName(),
            order.getService().getCompany().getUsername(),
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

    private ServiceOrder loadOrder(Long orderId, UserAll user) {
        return serviceOrderRepository.findById(orderId)
            .filter(order -> order.getUser() != null && Objects.equals(order.getUser().getId(), user.getId()))
            .orElseThrow(() -> new RuntimeException("未找到对应订单"));
    }

    private UserAll ensureUser() {
        UserAll user = accountLookupService.getCurrentAccount();
        if (!AccountRole.USER.getLabel().equals(user.getUserType())) {
            throw new RuntimeException("仅普通用户可访问消息中心");
        }
        return user;
    }
}
