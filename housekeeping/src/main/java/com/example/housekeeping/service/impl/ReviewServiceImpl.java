package com.example.housekeeping.service.impl;

import com.example.housekeeping.domain.entity.HousekeepingService;
import com.example.housekeeping.domain.entity.ServiceAppointment;
import com.example.housekeeping.domain.entity.ServiceProvider;
import com.example.housekeeping.domain.entity.ServiceReview;
import com.example.housekeeping.domain.enums.AppointmentStatus;
import com.example.housekeeping.dto.ReviewReplyRequest;
import com.example.housekeeping.dto.ServiceReviewRequest;
import com.example.housekeeping.repository.ServiceAppointmentRepository;
import com.example.housekeeping.repository.ServiceReviewRepository;
import com.example.housekeeping.service.ReviewService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ServiceReviewRepository serviceReviewRepository;
    private final ServiceAppointmentRepository serviceAppointmentRepository;

    @Override
    public ServiceReview submitReview(Long userId, ServiceReviewRequest request) {
        ServiceAppointment appointment = serviceAppointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "预约不存在"));
        if (appointment.getUser() == null || !appointment.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权评价该预约");
        }
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "服务未完成，无法评价");
        }
        serviceReviewRepository.findByAppointmentId(request.appointmentId())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "该预约已评价");
                });
        ServiceReview review = new ServiceReview();
        review.setAppointment(appointment);
        review.setUser(appointment.getUser());
        review.setProvider(appointment.getProvider());
        review.setService(appointment.getService());
        review.setRating(request.rating());
        review.setComment(request.comment());
        ServiceReview saved = serviceReviewRepository.save(review);
        recalculateServiceRating(appointment.getService());
        if (appointment.getProvider() != null) {
            recalculateProviderRating(appointment.getProvider());
        }
        return saved;
    }

    @Override
    public ServiceReview replyReview(Long reviewId, ReviewReplyRequest request, Long providerId) {
        ServiceReview review = serviceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "评价不存在"));
        if (review.getProvider() == null || !review.getProvider().getId().equals(providerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权回复该评价");
        }
        review.setReply(request.reply());
        review.setRepliedAt(LocalDateTime.now());
        return review;
    }

    @Override
    public List<ServiceReview> getReviewsForService(Long serviceId) {
        return serviceReviewRepository.findByServiceId(serviceId);
    }

    @Override
    public List<ServiceReview> getReviewsForProvider(Long providerId) {
        return serviceReviewRepository.findByProviderId(providerId);
    }

    @Override
    public List<ServiceReview> getReviewsForUser(Long userId) {
        return serviceReviewRepository.findByUserId(userId);
    }

    private void recalculateServiceRating(HousekeepingService service) {
        if (service == null) {
            return;
        }
        List<ServiceReview> reviews = serviceReviewRepository.findByServiceId(service.getId());
        if (reviews.isEmpty()) {
            service.setRating(null);
            return;
        }
        double average = reviews.stream().mapToInt(ServiceReview::getRating).average().orElse(0.0);
        service.setRating(Math.round(average * 10.0) / 10.0);
    }

    private void recalculateProviderRating(ServiceProvider provider) {
        if (provider == null) {
            return;
        }
        List<ServiceReview> reviews = serviceReviewRepository.findByProviderId(provider.getId());
        if (reviews.isEmpty()) {
            provider.setRating(null);
            return;
        }
        double average = reviews.stream().mapToInt(ServiceReview::getRating).average().orElse(0.0);
        provider.setRating(Math.round(average * 10.0) / 10.0);
    }
}
