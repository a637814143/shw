package com.example.housekeeping.service;

import com.example.housekeeping.domain.entity.ServiceReview;
import com.example.housekeeping.dto.ReviewReplyRequest;
import com.example.housekeeping.dto.ServiceReviewRequest;
import java.util.List;

public interface ReviewService {
    ServiceReview submitReview(Long userId, ServiceReviewRequest request);

    ServiceReview replyReview(Long reviewId, ReviewReplyRequest request, Long providerId);

    List<ServiceReview> getReviewsForService(Long serviceId);

    List<ServiceReview> getReviewsForProvider(Long providerId);

    List<ServiceReview> getReviewsForUser(Long userId);
}
