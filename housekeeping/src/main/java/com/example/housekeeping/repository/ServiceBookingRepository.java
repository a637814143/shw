package com.example.housekeeping.repository;

import com.example.housekeeping.model.ServiceBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {

    List<ServiceBooking> findAllByOrderByCreatedAtDesc();

    List<ServiceBooking> findByCreatedByOrderByCreatedAtDesc(String createdBy);

    List<ServiceBooking> findByAssignedProviderOrderByCreatedAtDesc(String assignedProvider);
}
