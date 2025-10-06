package com.example.housekeeping.repository;

import com.example.housekeeping.domain.entity.ServiceAppointment;
import com.example.housekeeping.domain.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment, Long> {
    List<ServiceAppointment> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<ServiceAppointment> findByProviderIdOrderByCreatedAtDesc(Long providerId);
    long countByStatus(AppointmentStatus status);
    List<ServiceAppointment> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
}
