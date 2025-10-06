package com.example.housekeeping.controller;

import com.example.housekeeping.domain.entity.ServiceReview;
import com.example.housekeeping.dto.ReviewReplyRequest;
import com.example.housekeeping.dto.ServiceReviewRequest;
import com.example.housekeeping.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/user/{userId}")
    public ServiceReview submitReview(@PathVariable Long userId, @Valid @RequestBody ServiceReviewRequest request) {
        return reviewService.submitReview(userId, request);
    }

    @PostMapping("/{reviewId}/reply")
    public ServiceReview replyReview(@PathVariable Long reviewId,
            @RequestParam Long providerId,
            @Valid @RequestBody ReviewReplyRequest request) {
        return reviewService.replyReview(reviewId, request, providerId);
    }

    @GetMapping("/service/{serviceId}")
    public List<ServiceReview> getServiceReviews(@PathVariable Long serviceId) {
        return reviewService.getReviewsForService(serviceId);
    }

    @GetMapping("/provider/{providerId}")
    public List<ServiceReview> getProviderReviews(@PathVariable Long providerId) {
        return reviewService.getReviewsForProvider(providerId);
    }

    @GetMapping("/user/{userId}")
    public List<ServiceReview> getUserReviews(@PathVariable Long userId) {
        return reviewService.getReviewsForUser(userId);
    }
}
