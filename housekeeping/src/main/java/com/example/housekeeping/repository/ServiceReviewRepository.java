package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.ServiceReview;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Long> {
    List<ServiceReview> findByProviderId(Long providerId);
    List<ServiceReview> findByServiceId(Long serviceId);
    Optional<ServiceReview> findByAppointmentId(Long appointmentId);
    List<ServiceReview> findByUserId(Long userId);
}
