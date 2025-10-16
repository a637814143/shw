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
import java.util.List;
import java.util.stream.Collectors;

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
        review.setCreatedAt(Instant.now());

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

    private void ensureRole(UserAll account, AccountRole expectedRole) {
        if (!expectedRole.getLabel().equals(account.getUserType())) {
            throw new RuntimeException("权限不足");
        }
    }

    private ServiceReviewResponse mapToResponse(ServiceReview review) {
        return new ServiceReviewResponse(
            review.getId(),
            review.getService().getId(),
            review.getService().getName(),
            review.getUser().getUsername(),
            review.getRating(),
            review.getContent(),
            review.getCreatedAt()
        );
    }
}
