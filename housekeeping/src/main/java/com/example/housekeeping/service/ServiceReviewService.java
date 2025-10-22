package com.example.housekeeping.service;

import com.example.housekeeping.dto.ServiceReviewRequest;
import com.example.housekeeping.dto.ServiceReviewResponse;
import com.example.housekeeping.entity.HousekeepService;
import com.example.housekeeping.entity.ServiceReview;
import com.example.housekeeping.entity.UserAll;
import com.example.housekeeping.enums.AccountRole;
import com.example.housekeeping.repository.HousekeepServiceRepository;
import com.example.housekeeping.repository.ServiceReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 用户评价相关业务。
 */
@Service
public class ServiceReviewService {

    @Autowired
    private ServiceReviewRepository serviceReviewRepository;

    @Autowired
    private HousekeepServiceRepository housekeepServiceRepository;

    @Autowired
    private AccountLookupService accountLookupService;

    @Transactional
    public ServiceReviewResponse createReview(ServiceReviewRequest request) {
        UserAll user = accountLookupService.getCurrentAccount();
        ensureRole(user, AccountRole.USER);

        HousekeepService service = housekeepServiceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new RuntimeException("服务不存在"));

        ServiceReview review = new ServiceReview();
        review.setService(service);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        Instant now = Instant.now();
        review.setCreatedAt(now);
        review.setUpdatedAt(now);

        return mapToResponse(serviceReviewRepository.save(review));
    }

    @Transactional(readOnly = true)
    public List<ServiceReviewResponse> listReviewsForService(Long serviceId) {
        HousekeepService service = housekeepServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("服务不存在"));
        return serviceReviewRepository.findByServiceOrderByCreatedAtDesc(service).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceReviewResponse> listReviewsForCurrentCompany(String keyword) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);

        List<HousekeepService> services = housekeepServiceRepository.findByCompany(company);
        if (services.isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedKeyword = normalizeKeyword(keyword);

        return serviceReviewRepository.findByServiceInOrderByCreatedAtDesc(services).stream()
            .filter(review -> matchesReviewKeyword(review, normalizedKeyword))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServiceReviewResponse> listReviewsForCurrentUser(String keyword) {
        UserAll user = accountLookupService.getCurrentAccount();
        ensureRole(user, AccountRole.USER);

        String normalizedKeyword = normalizeKeyword(keyword);

        return serviceReviewRepository.findByUserOrderByCreatedAtDesc(user).stream()
            .filter(review -> matchesReviewKeyword(review, normalizedKeyword))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReviewsForCurrentCompany(List<Long> ids) {
        UserAll company = accountLookupService.getCurrentAccount();
        ensureRole(company, AccountRole.COMPANY);
        if (ids == null || ids.isEmpty()) {
            return;
        }

        Set<Long> allowedServiceIds = housekeepServiceRepository.findByCompany(company).stream()
            .map(HousekeepService::getId)
            .collect(Collectors.toSet());
        if (allowedServiceIds.isEmpty()) {
            return;
        }

        Set<Long> distinctIds = new HashSet<>(ids);
        List<ServiceReview> reviews = serviceReviewRepository.findAllById(distinctIds);
        List<ServiceReview> permitted = reviews.stream()
            .filter(review -> review.getService() != null)
            .filter(review -> allowedServiceIds.contains(review.getService().getId()))
            .collect(Collectors.toList());
        if (permitted.isEmpty()) {
            return;
        }
        serviceReviewRepository.deleteAll(permitted);
    }

    @Transactional
    public void deleteReviewsForCurrentUser(List<Long> ids) {
        UserAll user = accountLookupService.getCurrentAccount();
        ensureRole(user, AccountRole.USER);
        if (ids == null || ids.isEmpty()) {
            return;
        }

        Set<Long> distinctIds = new HashSet<>(ids);
        List<ServiceReview> reviews = serviceReviewRepository.findAllById(distinctIds);
        List<ServiceReview> permitted = reviews.stream()
            .filter(review -> review.getUser() != null && Objects.equals(review.getUser().getId(), user.getId()))
            .collect(Collectors.toList());
        if (permitted.isEmpty()) {
            return;
        }
        serviceReviewRepository.deleteAll(permitted);
    }

    private void ensureRole(UserAll account, AccountRole expectedRole) {
        if (!expectedRole.getLabel().equals(account.getUserType())) {
            throw new RuntimeException("权限不足");
        }
    }

    private String normalizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String trimmed = keyword.trim();
        return trimmed.isEmpty() ? null : trimmed.toLowerCase();
    }

    private boolean matchesReviewKeyword(ServiceReview review, String keyword) {
        if (keyword == null) {
            return true;
        }
        return Stream.of(
                review.getService() != null ? review.getService().getName() : null,
                review.getUser() != null ? review.getUser().getUsername() : null,
                review.getContent(),
                review.getCompanyReply()
            )
            .filter(Objects::nonNull)
            .map(String::toLowerCase)
            .anyMatch(value -> value.contains(keyword));
    }

    private ServiceReviewResponse mapToResponse(ServiceReview review) {
        return new ServiceReviewResponse(
            review.getId(),
            review.getService().getId(),
            review.getService().getName(),
            review.getUser().getUsername(),
            review.getRating(),
            review.getContent(),
            review.getCreatedAt(),
            review.getUpdatedAt(),
            review.getCompanyReply(),
            review.getReplyAt(),
            review.getPinned()
        );
    }
}
